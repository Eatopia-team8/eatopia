package org.example.eatopia.domain.payment.service.command;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.payment.dto.event.PaymentCompletedEvent;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentUpdateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentVerifyRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.entity.PaymentStatus;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.example.eatopia.domain.payment.validator.PaymentValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandServiceImpl implements PaymentCommandService {

    private final OrderQueryService orderQueryService;

    private final PaymentRepository paymentRepository;

    private final IamportClient iamportClient;
    private final PaymentValidator paymentValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PaymentResponse createPayment(Long userId, PaymentCreateRequest request) {
        Order order = paymentValidator.paymentCreateValidate(userId, request.orderId());

        Payment payment = Payment.create(order, request.paymentMethod());
        Payment savedPayment = paymentRepository.save(payment);

        return PaymentResponse.from(savedPayment);
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(Long userId, PaymentVerifyRequest request) throws IamportResponseException, IOException {
        Payment payment = paymentValidator.verifyPayment(userId, request);

        payment.updateStatus(PaymentStatus.SUCCESS);
        eventPublisher.publishEvent(new PaymentCompletedEvent(payment.getOrder().getId(), payment.getOrder().getUser().getId()));

        return PaymentResponse.from(payment);
    }

    @Override
    public void cancelPaymentByOrder(Order order) {
        paymentRepository.findByOrder(order).ifPresent(payment -> {
            paymentValidator.paymentCancelValidate(payment);
            PaymentStatus originalStatus = payment.getStatus();
            payment.updateStatus(PaymentStatus.CANCELED);

            // TODO: [구현 필요] PortOne 환불 API 호출
        });
    }

    @Override
    public PaymentResponse updatePaymentMethod(Long userId, Long paymentId, PaymentUpdateRequest request) {
        Payment payment = paymentValidator.paymentUpdateValidate(userId, paymentId);

        payment.updateMethod(request.paymentMethod());

        return PaymentResponse.from(payment);
    }
}
