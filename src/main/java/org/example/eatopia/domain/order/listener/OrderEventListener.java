package org.example.eatopia.domain.order.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.order.dto.event.OrderCancelledEvent;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.service.command.OrderCommandService;
import org.example.eatopia.domain.payment.dto.event.PaymentCompletedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderCommandService orderCommandService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("주문 성공 처리 중 Order ID: {}", event.orderId());
        orderCommandService.successOrder(event.userId(), event.orderId());
        log.info("주문 성공 처리가 완료되었습니다. Order ID: {}", event.orderId());
    }

    @TransactionalEventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        Order order = event.order();
        log.info("주문 취소 처리가 완료되었습니다. Order ID: {}", order.getId());
    }
}
