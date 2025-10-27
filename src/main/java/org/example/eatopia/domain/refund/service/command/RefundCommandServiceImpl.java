package org.example.eatopia.domain.refund.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.service.query.OrderDetailQueryService;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.service.query.PaymentQueryService;
import org.example.eatopia.domain.product.service.command.ProductCommandService;
import org.example.eatopia.domain.refund.dto.event.RefundSuccessEvent;
import org.example.eatopia.domain.refund.dto.request.RefundCreateRequest;
import org.example.eatopia.domain.refund.dto.response.RefundResponse;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.refund.enums.RefundStatus;
import org.example.eatopia.domain.refund.exception.RefundErrorCode;
import org.example.eatopia.domain.refund.exception.RefundException;
import org.example.eatopia.domain.refund.repository.RefundRepository;
import org.example.eatopia.domain.refund.validator.RefundValidator;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class RefundCommandServiceImpl implements RefundCommandService {

    private final RefundRepository refundRepository;

    private final UserQueryService userQueryService;
    private final ProductCommandService productCommandService;
    private final OrderDetailQueryService orderDetailQueryService;
    private final PaymentQueryService paymentQueryService;

    private final RefundValidator refundValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public RefundResponse requestRefund(Long userId, RefundCreateRequest request) {

        User user = userQueryService.getUserEntityById(userId);
        OrderDetail orderDetail = orderDetailQueryService.getOrderDetailEntityById(request.orderDetailId());
        Payment payment = paymentQueryService.getPaymentEntityByOrder(orderDetail.getOrder());

        refundValidator.validateRefundRequest(user, orderDetail);

        BigDecimal refundAmount = orderDetail.getPrice();

        Refund refund = Refund.of(user, payment, orderDetail, refundAmount, request.reason());
        refund.updateStatus(RefundStatus.PENDING);
        Refund savedRefund = refundRepository.save(refund);

        return RefundResponse.from(savedRefund);
    }

    @Override
    public RefundResponse successRefund(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException(RefundErrorCode.REFUND_NOT_FOUND));

        refundValidator.validateRefundStatusPending(refund);

        OrderDetail orderDetail = refund.getOrderDetail();

        productCommandService.increaseStock(
                orderDetail.getProduct().getId(),
                orderDetail.getQuantity()
        );

        refund.updateStatus(RefundStatus.SUCCESS);

        eventPublisher.publishEvent(new RefundSuccessEvent(refund));
        //outbox 패턴 추가

        return RefundResponse.from(refund);
    }

    @Override
    public RefundResponse canceledRefund(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundException(RefundErrorCode.REFUND_NOT_FOUND));

        refundValidator.validateRefundStatusPending(refund);

        refund.updateStatus(RefundStatus.CANCELED);

        return RefundResponse.from(refund);
    }
}