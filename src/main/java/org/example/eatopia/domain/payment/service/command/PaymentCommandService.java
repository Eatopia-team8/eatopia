package org.example.eatopia.domain.payment.service.command;

import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentUpdateRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;

public interface PaymentCommandService {

    PaymentResponse createPayment(Long userId, PaymentCreateRequest request);

    void cancelPaymentByOrder(Order order);

    PaymentResponse updatePaymentMethod(Long userId, Long paymentId, PaymentUpdateRequest request);
}
