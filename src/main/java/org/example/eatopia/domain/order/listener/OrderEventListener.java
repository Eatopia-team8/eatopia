package org.example.eatopia.domain.order.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.order.service.command.OrderCommandService;
import org.example.eatopia.domain.payment.dto.event.PaymentCompletedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    private final OrderCommandService orderCommandService;

    @TransactionalEventListener
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        orderCommandService.successOrder(event.userId(), event.orderId());
    }
}
