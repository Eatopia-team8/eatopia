package org.example.eatopia.domain.payment.service.query;

import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.entity.Payment;

public interface PaymentQueryService {

    Payment getPaymentEntityByOrder(Order order);
}
