package org.example.eatopia.domain.refund.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.entity.OrderStatus;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.enums.RefundStatus;
import org.example.eatopia.domain.refund.exception.RefundErrorCode;
import org.example.eatopia.domain.refund.exception.RefundException;
import org.example.eatopia.domain.refund.repository.RefundRepository;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundValidator {

    private final RefundRepository refundRepository;

    public void validateRefundRequest(User user, OrderDetail orderDetail) {

        // 주문자 확인
        if (!orderDetail.getOrder().getUser().getId().equals(user.getId())) {
            throw new RefundException(RefundErrorCode.REFUND_FORBIDDEN);
        }

        // 결제 완료
        if (orderDetail.getOrder().getStatus() != OrderStatus.SUCCESS) {
            throw new RefundException(RefundErrorCode.ORDER_NOT_SUCCESSFUL);
        }

        // 환불 확인
        if (refundRepository.existsByOrderDetailId(orderDetail.getId())) {
            throw new RefundException(RefundErrorCode.ALREADY_REFUNDED);
        }
    }

    //pending 확인
    public void validateRefundStatusPending(Refund refund) {
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new RefundException(RefundErrorCode.REFUND_NOT_PENDING);
        }
    }
}