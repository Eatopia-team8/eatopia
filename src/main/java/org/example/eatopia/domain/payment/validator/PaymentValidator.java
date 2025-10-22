package org.example.eatopia.domain.payment.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.entity.PaymentStatus;
import org.example.eatopia.domain.payment.exception.PaymentErrorCode;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final OrderQueryService orderQueryService;
    private final PaymentRepository paymentRepository;

    public Order paymentCreateValidate(Long userId, Long orderId) {
        Order order = orderQueryService.findOrderByUserAndId(userId, orderId);

        paymentRepository.findByOrder(order).ifPresent(payment -> {
            throw new GlobalException(PaymentErrorCode.ALREADY_PAID_ORDER);
        });

        return order;
    }

    /**
     * 성공한 결제만 취소 가능
     */
    public void paymentCancelValidate(Payment payment) {
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new GlobalException(PaymentErrorCode.CANNOT_CANCEL_PAYMENT);
        }

        if (payment.getStatus() == PaymentStatus.PENDING) {
            throw new GlobalException(PaymentErrorCode.CANNOT_CANCEL_PAYMENT);
        }
    }

    public Payment paymentUpdateValidate(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findByOrderUserIdAndId(userId, paymentId)
                .orElseThrow(() -> new GlobalException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new GlobalException(PaymentErrorCode.CANNOT_UPDATE_METHOD);
        }

        return payment;
    }
}
