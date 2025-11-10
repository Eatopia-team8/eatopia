package org.example.eatopia.domain.settlement.service;

import org.example.eatopia.common.infra.portone.PortonePayoutClient;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.service.command.OrderCommandService;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.service.command.RefundCommandService;
import org.example.eatopia.domain.refund.service.query.RefundQueryService;
import org.example.eatopia.domain.settlement.dto.request.SettlementCreateRequest;
import org.example.eatopia.domain.settlement.dto.response.SettlementResponse;
import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.enums.BankCode;
import org.example.eatopia.domain.settlement.enums.SettlementStatus;
import org.example.eatopia.domain.settlement.exception.SettlementException;
import org.example.eatopia.domain.settlement.repository.SettlementRepository;
import org.example.eatopia.domain.settlement.service.command.SettlementCommandServiceImpl;
import org.example.eatopia.domain.settlement.service.command.SettlementTransactionalService;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SettlementCommandService 테스트")
class SettlementCommandServiceTest {

    @InjectMocks
    private SettlementCommandServiceImpl settlementCommandService;

    @Mock
    private SettlementRepository settlementRepository;
    @Mock
    private UserQueryService userQueryService;
    @Mock
    private PortonePayoutClient portonePayoutClient;
    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private RefundQueryService refundQueryService;
    @Mock
    private OrderCommandService orderCommandService;
    @Mock
    private RefundCommandService refundCommandService;
    @Mock
    private SettlementTransactionalService settlementTransactionalService;

    private User seller;
    private SettlementCreateRequest request;
    private OrderDetail orderDetail;
    private Refund refund;

    @BeforeEach
    void setUp() {

        seller = User.signUp("seller@test.com", "password", "TestSeller", UserRole.SELLER);
        ReflectionTestUtils.setField(seller, "id", 1L);

        request = new SettlementCreateRequest(
                BankCode.KAKAO_BANK,
                "1234-56-78901",
                "예금주명"
        );

        Category mockCategory = Category.create("test", null);

        Product mockProduct = Product.create(
                "TestProduct",
                "Desc",
                new BigDecimal("20000"), // 상품 가격
                10,
                ProductStatus.AVAILABLE,
                mockCategory,
                seller
        );

        User buyer = User.signUp("buyer@test.com", "password", "TestBuyer", UserRole.BUYER);
        Order order = Order.create(buyer, "ORDER-CODE", BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.TEN, null, "Address");

        // 매출 20000 (수량 1, 가격 20000), 수수료 1000 (20000 * 0.05)
        orderDetail = OrderDetail.create(order, mockProduct, 1, new BigDecimal("20000"), new BigDecimal("1000"));
        ReflectionTestUtils.setField(orderDetail, "id", 1000L);

        // 환불액 10000, 환불 수수료 500
        refund = Refund.of(buyer, null, null, new BigDecimal("10000"), 1, null);
        ReflectionTestUtils.setField(refund, "id", 2000L);
        ReflectionTestUtils.setField(refund, "amount", new BigDecimal("10000"));
        ReflectionTestUtils.setField(refund, "commissionAmount", new BigDecimal("500"));
    }

    @Test
    @DisplayName("성공: 정산 요청 시 Settlement 생성 및 PENDING 상태 확인 (최종 정산액 9500)")
    void requestSettlement_Success_CreatesPendingSettlement() {
        // Given
        // 최종 정산액 = (총매출 20000 - 총환불 10000) - (총수수료 1000 - 환불수수료 500) = 9500
        given(userQueryService.getUserEntityById(1L)).willReturn(seller);
        given(orderQueryService.getUnsettleOrderDetails(1L)).willReturn(List.of(orderDetail));
        given(refundQueryService.getUnsettleRefunds(1L)).willReturn(List.of(refund));

        Settlement pendingSettlement = Settlement.create(seller, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pendingSettlement, "id", 999L);
        given(settlementRepository.save(any(Settlement.class))).willReturn(pendingSettlement);

        // When
        SettlementResponse response = settlementCommandService.requestSettlement(1L, request);

        // Then
        assertNotNull(response);
        assertEquals(SettlementStatus.PENDING, response.status());

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        then(settlementRepository).should(times(1)).save(settlementCaptor.capture());

        Settlement savedSettlement = settlementCaptor.getValue();
        assertEquals(new BigDecimal("9500"), savedSettlement.getFinalSettlementAmount());
        assertEquals(new BigDecimal("20000"), savedSettlement.getTotalSaleAmount());

        // 정산 대상 연결
        then(orderCommandService).should(times(1)).settlementToOrderDetails(any(), any());
        then(refundCommandService).should(times(1)).settlementToRefunds(any(), any());
    }

    @Test
    @DisplayName("실패: 정산할 내역이 없는 경우 예외 발생")
    void requestSettlement_Failure_NothingToSettle() {
        // Given
        given(userQueryService.getUserEntityById(1L)).willReturn(seller);
        given(orderQueryService.getUnsettleOrderDetails(1L)).willReturn(Collections.emptyList());
        given(refundQueryService.getUnsettleRefunds(1L)).willReturn(Collections.emptyList());

        // When & Then
        assertThrows(SettlementException.class, () ->
                settlementCommandService.requestSettlement(1L, request)
        );
        then(settlementRepository).should(never()).save(any(Settlement.class));
    }

    @Test
    @DisplayName("성공: 정산 처리 시 Portone 호출 및 완료 트랜잭션 호출 검증")
    void processPayout_Success_VerifyTransactionCalls() {
        // Given
        Settlement pendingSettlement = Settlement.create(seller, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pendingSettlement, "id", 999L);
        given(settlementRepository.findById(999L)).willReturn(Optional.of(pendingSettlement));

        final String expectedImpUid = "imp_payout_success_uid";
        // Portone 성공 Mock
        given(portonePayoutClient.requestPayout(anyString(), any(BigDecimal.class), eq(request.bankCode().getCode()), anyString(), anyString())).willReturn(expectedImpUid);

        // When
        settlementCommandService.processPayout(999L, request);

        // Then
        // Portone 호출 확인
        then(portonePayoutClient).should(times(1)).requestPayout(anyString(), eq(pendingSettlement.getFinalSettlementAmount()), eq(request.bankCode().getCode()), eq(request.bankAccount()), eq(request.bankHolderName()));

        // 완료 트랜잭션 호출 확인
        then(settlementTransactionalService).should(times(1)).completeSettlementInNewTransaction(
                eq(999L),
                eq(expectedImpUid),
                eq(request)
        );
        then(settlementTransactionalService).should(never()).failSettlementInNewTransaction(anyLong(), anyString());
    }

    @Test
    @DisplayName("실패: Portone API 오류 발생 시 실패 트랜잭션 호출 검증")
    void processPayout_Failure_PortoneError_VerifyTransactionCalls() {
        // Given
        Settlement pendingSettlement = Settlement.create(seller, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        ReflectionTestUtils.setField(pendingSettlement, "id", 999L);
        given(settlementRepository.findById(999L)).willReturn(Optional.of(pendingSettlement));

        // Portone API 호출 실패 Mock
        given(portonePayoutClient.requestPayout(anyString(), any(BigDecimal.class), anyString(), anyString(), anyString()))
                .willThrow(new RuntimeException("Portone connection failed"));

        // When
        settlementCommandService.processPayout(999L, request);

        // Then
        // Portone 호출 확인
        then(portonePayoutClient).should(times(1)).requestPayout(anyString(), any(BigDecimal.class), anyString(), anyString(), anyString());

        // 실패 트랜잭션 호출 확인
        ArgumentCaptor<String> reasonCaptor = ArgumentCaptor.forClass(String.class);
        then(settlementTransactionalService).should(times(1)).failSettlementInNewTransaction(eq(999L), reasonCaptor.capture());
        assertTrue(reasonCaptor.getValue().contains("Portone API 호출에 실패했습니다."));

        // 완료 트랜잭션은 호출되지 않아야 함
        then(settlementTransactionalService).should(never()).completeSettlementInNewTransaction(anyLong(), anyString(), any());
    }
}