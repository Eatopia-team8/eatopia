package org.example.eatopia.domain.payment.service.command;

import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;

public interface PaymentCommandService {

    PaymentResponse createPayment(Long userId, PaymentCreateRequest request);
}
