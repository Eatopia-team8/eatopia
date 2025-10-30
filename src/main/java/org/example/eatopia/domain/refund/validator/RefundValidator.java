package org.example.eatopia.domain.refund.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.enums.OrderStatus;
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

    private static final long REFUND_DEADLINE = 7;
    private final RefundRepository refundRepository;

    public void validateRefundRequest(User user, OrderDetail orderDetail, Integer quantity) {

        Order order = orderDetail.getOrder();

        // 주문자 확인
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RefundException(RefundErrorCode.REFUND_FORBIDDEN);
        }

        // 결제 완료
        if (order.getStatus() != OrderStatus.SUCCESS) {
            throw new RefundException(RefundErrorCode.ORDER_NOT_SUCCESSFUL);
        }

        //기한 확인
        LocalDateTime orderCompletedAt = order.getUpdatedAt();
        if (orderCompletedAt != null) {
            LocalDateTime deadline = orderCompletedAt.plusDays(REFUND_DEADLINE);
            if (LocalDateTime.now().isAfter(deadline)) {
                throw new RefundException(RefundErrorCode.REFUND_PERIOD_EXPIRED);
            }
        } else {
            throw new RefundException(RefundErrorCode.REFUND_NOT_ALLOWED);
        }

        // 환불 확인
        if (refundRepository.existsByOrderDetailId(orderDetail.getId())) {
            throw new RefundException(RefundErrorCode.ALREADY_REFUNDED);
        }

        if (quantity == null || quantity <= 0) {
            throw new RefundException(RefundErrorCode.INVALID_REFUND_QUANTITY);
        }

        if (quantity > orderDetail.getQuantity()) {
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