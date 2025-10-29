package org.example.eatopia.domain.payment.service.command;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.dto.event.PaymentCompletedEvent;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentUpdateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentVerifyRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.enums.PaymentStatus;
import org.example.eatopia.domain.payment.exception.PaymentErrorCode;
import org.example.eatopia.domain.payment.exception.PaymentException;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.example.eatopia.domain.payment.validator.PaymentValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandServiceImpl implements PaymentCommandService {

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

        payment.completePayment(request.impUid());
        eventPublisher.publishEvent(new PaymentCompletedEvent(payment.getOrder().getId(), payment.getOrder().getUser().getId()));

        return PaymentResponse.from(payment);
    }

    @Override
    public void cancelPaymentByOrder(Order order) {
        paymentRepository.findByOrder(order).ifPresent(payment -> {
            paymentValidator.paymentCancelValidate(payment);
            try {
                CancelData cancelData = new CancelData(
                        payment.getImpUid(),
                        true,
                        payment.getPrice()
                );

                log.info("환불 API [Payment ID: {}, ImpUid: {}]", payment.getId(), payment.getImpUid());
                iamportClient.cancelPaymentByImpUid(cancelData);

                log.info("환불 API 성공 [Payment ID: {}, ImpUid: {}]", payment.getId(), payment.getImpUid());
                payment.updateStatus(PaymentStatus.CANCELED);
            } catch (IamportResponseException | IOException e) {
                log.error("환불 API 실패 [Payment ID: {}, ImpUid: {}] - 오류: {}", payment.getId(), payment.getImpUid(), e.getMessage(), e);
                throw new PaymentException(PaymentErrorCode.PAYMENT_API_ERROR);
            } catch (Exception e) {
                log.error("결제 취소 처리 중 오류 발생 [Payment ID: {}] - 오류: {}", payment.getId(), e.getMessage(), e);
                throw new PaymentException(PaymentErrorCode.PAYMENT_CANCELED_FAILED);
            }
        });
    }

    @Override
    public PaymentResponse updatePaymentMethod(Long userId, Long paymentId, PaymentUpdateRequest request) {
        Payment payment = paymentValidator.paymentUpdateValidate(userId, paymentId);

        payment.updateMethod(request.paymentMethod());

        return PaymentResponse.from(payment);
    }
}
