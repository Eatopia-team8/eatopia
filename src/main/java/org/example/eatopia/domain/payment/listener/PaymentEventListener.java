package org.example.eatopia.domain.payment.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.order.dto.event.OrderCancelledEvent;
import org.example.eatopia.domain.payment.service.command.PaymentCommandService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PaymentCommandService paymentCommandService;

    @TransactionalEventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        paymentCommandService.cancelPaymentByOrder(event.order());
    }
}
