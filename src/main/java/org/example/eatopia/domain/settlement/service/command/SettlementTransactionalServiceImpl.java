package org.example.eatopia.domain.settlement.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.service.command.OrderCommandService;
import org.example.eatopia.domain.refund.service.command.RefundCommandService;
import org.example.eatopia.domain.settlement.dto.request.SettlementCreateRequest;
import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.exception.SettlementErrorCode;
import org.example.eatopia.domain.settlement.exception.SettlementException;
import org.example.eatopia.domain.settlement.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementTransactionalServiceImpl implements SettlementTransactionalService {

    private final SettlementRepository settlementRepository;
    private final OrderCommandService orderCommandService;
    private final RefundCommandService refundCommandService;

    @Override
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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failSettlementInNewTransaction(Long settlementId, String failReason) {
        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));

        settlement.fail(failReason);

        orderCommandService.rollbackSettlementForOrderDetails(settlement.getOrderDetails());
        refundCommandService.rollbackSettlementForRefunds(settlement.getRefunds());
    }
}