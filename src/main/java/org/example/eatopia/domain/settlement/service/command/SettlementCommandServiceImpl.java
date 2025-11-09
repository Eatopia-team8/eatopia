package org.example.eatopia.domain.settlement.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.common.infra.portone.PortonePayoutClient;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.service.command.OrderCommandService;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.service.command.RefundCommandService;
import org.example.eatopia.domain.refund.service.query.RefundQueryService;
import org.example.eatopia.domain.settlement.dto.request.SettlementCreateRequest;
import org.example.eatopia.domain.settlement.dto.response.SettlementResponse;
import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.enums.SettlementStatus;
import org.example.eatopia.domain.settlement.exception.SettlementErrorCode;
import org.example.eatopia.domain.settlement.exception.SettlementException;
import org.example.eatopia.domain.settlement.repository.SettlementRepository;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class SettlementCommandServiceImpl implements SettlementCommandService {

    private final SettlementRepository settlementRepository;
    private final UserQueryService userQueryService;
    private final PortonePayoutClient portonePayoutClient;
    private final OrderQueryService orderQueryService;
    private final RefundQueryService refundQueryService;
    private final OrderCommandService orderCommandService;
    private final RefundCommandService refundCommandService;

    @Override
    @Transactional
    public Long requestSettlement(Long sellerId, SettlementCreateRequest request) {

        User seller = userQueryService.getUserEntityById(sellerId);
        if (seller.getUserRole() != UserRole.SELLER) {
            throw new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND);
        }

        List<OrderDetail> detailsToSettle = orderQueryService.getUnsettleOrderDetails(sellerId);
        List<Refund> refundsToSettle = refundQueryService.getUnsettleRefunds(sellerId);

        if (detailsToSettle.isEmpty() && refundsToSettle.isEmpty()) {
            throw new SettlementException(SettlementErrorCode.NOTHING_TO_SETTLE);
        }

        BigDecimal totalSaleAmount = detailsToSettle.stream()
                .map(od -> od.getPrice().multiply(BigDecimal.valueOf(od.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissionAmount = detailsToSettle.stream()
                .map(OrderDetail::getCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRefundAmount = refundsToSettle.stream()
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCommissionRefundAmount = refundsToSettle.stream()
                .map(Refund::getCommissionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Settlement settlement = Settlement.create(
                seller,
                totalSaleAmount,
                totalCommissionAmount,
                totalRefundAmount,
                totalCommissionRefundAmount
        );


        Settlement savedSettlement = settlementRepository.save(settlement);

        linkDetailsToSettlement(detailsToSettle, savedSettlement);
        linkRefundsToSettlement(refundsToSettle, savedSettlement);

        return savedSettlement.getId();
    }

    @Override
    public SettlementResponse processPayout(Long settlementId, SettlementCreateRequest request) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        if (settlement.getStatus() != SettlementStatus.PENDING) {
            throw new SettlementException(SettlementErrorCode.INVALID_SETTLEMENT_STATUS);
        }

        String payoutUid = null;
        try {
            payoutUid = triggerPayout(settlement, request);

            completeSettlementInNewTransaction(settlement.getId(), payoutUid, request);

            settlement.complete(payoutUid, request.bankCode(), request.bankAccount(), request.bankHolderName());
            return SettlementResponse.from(settlement);

        } catch (Exception e) {
            log.error("정산 API 호출 또는 완료 처리 중 실패. [Settlement ID: {}]", settlement.getId(), e);
            failSettlementInNewTransaction(settlement.getId(), e.getMessage());

            throw new SettlementException(SettlementErrorCode.PAYOUT_API_ERROR);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeSettlementInNewTransaction(Long settlementId, String payoutUid, SettlementCreateRequest request) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        settlement.complete(
                payoutUid,
                request.bankCode(),
                request.bankAccount(),
                request.bankHolderName()
        );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // ✨ 새 트랜잭션 보장
    public void failSettlementInNewTransaction(Long settlementId, String failReason) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        // 1. Settlement 상태 FAILED로 변경
        settlement.fail(failReason);

        // 2. 연결된 OrderDetail/Refund에서 settlementId를 null로 롤백
        // (다음 정산 시도 시 다시 포함되도록)
        orderCommandService.rollbackSettlementForOrderDetails(settlement.getOrderDetails());
        refundCommandService.rollbackSettlementForRefunds(settlement.getRefunds());
    }

    private void linkDetailsToSettlement(List<OrderDetail> details, Settlement settlement) {
        if (details == null || details.isEmpty()) return;

        List<Long> orderDetailIds = details.stream().map(OrderDetail::getId).toList();
        orderCommandService.settlementToOrderDetails(orderDetailIds, settlement);
    }

    private void linkRefundsToSettlement(List<Refund> refunds, Settlement settlement) {
        if (refunds == null || refunds.isEmpty()) return;

        List<Long> refundIds = refunds.stream().map(Refund::getId).toList();
        refundCommandService.settlementToRefunds(refundIds, settlement);
    }

    private String triggerPayout(Settlement settlement, SettlementCreateRequest request) {

        String merchantPayoutUid = "payout_" + settlement.getId() + "_" + System.currentTimeMillis();

        try {
            String impUid = portonePayoutClient.requestPayout(
                    merchantPayoutUid,
                    settlement.getFinalSettlementAmount(),
                    request.bankCode().getCode(),
                    request.bankAccount(),
                    request.bankHolderName()
            );

            log.info("Portone Payout API (RestTemplate) 호출 성공: {}", impUid);
            return impUid;

        } catch (RuntimeException e) {
            log.error("Portone Payout API 호출 실패: [MerchantPayoutUID: {}], {}", merchantPayoutUid, e.getMessage(), e);
            throw new SettlementException(SettlementErrorCode.PAYOUT_API_ERROR);
        }
    }
}