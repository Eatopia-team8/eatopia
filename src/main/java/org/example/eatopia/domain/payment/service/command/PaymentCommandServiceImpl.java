package org.example.eatopia.domain.payment.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.dto.event.PaymentCompletedEvent;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.entity.PaymentStatus;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.example.eatopia.domain.payment.validator.PaymentValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final PaymentValidator paymentValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PaymentResponse createPayment(Long userId, PaymentCreateRequest request) {
        Order order = paymentValidator.paymentCreateValidate(userId, request.orderId());

        Payment payment = Payment.create(order, request.paymentMethod());

        //PG사 연동전까지 SUCCESS로 구현
        payment.updateStatus(PaymentStatus.SUCCESS);
        Payment savedPayment = paymentRepository.save(payment);
        eventPublisher.publishEvent(new PaymentCompletedEvent(order.getId(), order.getUserId()));

        return PaymentResponse.from(savedPayment);
    }

    @Override
    public void cancelPaymentByOrder(Order order) {
        paymentRepository.findByOrder(order).ifPresent(payment -> {
            paymentValidator.paymentCancelValidate(payment);
            payment.updateStatus(PaymentStatus.CANCELED);
        });
    }
}
