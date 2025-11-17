package org.example.eatopia.domain.refund.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.delivery.entity.Delivery;
import org.example.eatopia.domain.delivery.enums.DeliveryStatus;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.enums.PaymentStatus;
import org.example.eatopia.domain.payment.exception.PaymentErrorCode;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.enums.RefundStatus;
import org.example.eatopia.domain.refund.exception.RefundErrorCode;
import org.example.eatopia.domain.refund.exception.RefundException;
import org.example.eatopia.domain.refund.repository.RefundRepository;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RefundValidator {

    private static final long REFUND_DEADLINE = 3;
    private final RefundRepository refundRepository;

    public void validateRefundRequest(User user, OrderDetail orderDetail, Payment payment, Integer quantity) {

        Order order = orderDetail.getOrder();

        // 주문자 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RefundException(RefundErrorCode.REFUND_FORBIDDEN);
        }

        // 결제 완료
        if (order.getStatus() != OrderStatus.SUCCESS ||
                (payment.getStatus() != PaymentStatus.SUCCESS && payment.getStatus() != PaymentStatus.PARTIALLY_REFUND)) {
            throw new RefundException(RefundErrorCode.ORDER_NOT_SUCCESSFUL);
        }

        Delivery delivery = order.getDelivery();

        if (delivery == null) {
            throw new RefundException(RefundErrorCode.REFUND_NOT_ALLOWED);
        }

        // 배송이 완료되지 않았으면 환불 불가
        if (delivery.getStatus() != DeliveryStatus.DELIVERED) {
            throw new RefundException(RefundErrorCode.REFUND_NOT_ALLOWED_BEFORE_DELIVERY);
        }

        LocalDateTime deliveredAt = delivery.getDeliveredAt();
        if (deliveredAt == null) {
            throw new RefundException(RefundErrorCode.REFUND_NOT_ALLOWED);
        }

        LocalDateTime deadline = deliveredAt.plusDays(REFUND_DEADLINE);

        if (LocalDateTime.now().isAfter(deadline)) {
            throw new RefundException(RefundErrorCode.REFUND_PERIOD_EXPIRED);
        }

        //전체 환불인지 확인
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new GlobalException(PaymentErrorCode.ALREADY_REFUNDED);
        }
        //중복 요청 방지
        if (refundRepository.existsActiveRefundByOrderDetailId(orderDetail.getId())) {
            throw new RefundException(RefundErrorCode.ALREADY_REFUNDED);
        }

        //환불한 수량의 합
        int refundQuantity = refundRepository.sumSuccessQuantityByOrderDetailId(orderDetail.getId()).orElse(0);

        if (quantity == null || quantity <= 0) {
            throw new RefundException(RefundErrorCode.INVALID_REFUND_QUANTITY);
        }

        if (refundQuantity + quantity > orderDetail.getQuantity()) {
            throw new RefundException(RefundErrorCode.REFUND_QUANTITY_OVER);
        }
    }

    //pending 확인
    public void validateRefundStatusPending(Refund refund) {
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new RefundException(RefundErrorCode.REFUND_NOT_PENDING);
        }
    }
}