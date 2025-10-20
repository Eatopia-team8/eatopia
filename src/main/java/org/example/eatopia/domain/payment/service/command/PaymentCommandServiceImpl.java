package org.example.eatopia.domain.payment.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.example.eatopia.domain.payment.validator.PaymentValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentValidator paymentValidator;

    @Override
    public PaymentResponse createPayment(Long userId, PaymentCreateRequest request) {
        Order order = paymentValidator.paymentCreateValidate(userId, request.orderId());

        Payment payment = Payment.create(
                order,
                request.paymentMethod()
        );

        //peding -> success 추가 예정

        Payment savedPayment = paymentRepository.save(payment);

        PaymentResponse paymentResponse = PaymentResponse.from(savedPayment);
        return paymentResponse;
    }
}
