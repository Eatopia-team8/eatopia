package org.example.eatopia.domain.payment.validator;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.auth.exception.AuthErrorCode;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.payment.dto.request.PaymentVerifyRequest;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.entity.PaymentStatus;
import org.example.eatopia.domain.payment.exception.PaymentErrorCode;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentValidator {
    private final OrderQueryService orderQueryService;
    private final PaymentRepository paymentRepository;

    private final IamportClient iamportClient;

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

    public Payment verifyPayment(Long userId, PaymentVerifyRequest request) throws IamportResponseException, IOException {

        // PortOne API 호출: imp_uid로 실제 결제 내역 조회
        IamportResponse<com.siot.IamportRestClient.response.Payment> portoneResponse;
        try {
            portoneResponse = iamportClient.paymentByImpUid(request.impUid());
        } catch (IamportResponseException | IOException e) {
            throw new GlobalException(PaymentErrorCode.PAYMENT_API_ERROR, "PortOne API 조회 실패: " + e.getMessage());
        }

        // PortOne 응답 데이터 추출
        com.siot.IamportRestClient.response.Payment portonePayment = portoneResponse.getResponse();
        if (portonePayment == null) {
            throw new GlobalException(PaymentErrorCode.PORTONE_VERIFICATION_FAILED);
        }

        // status가 paid가 아닌 경우("paid", "ready", "failed")
        String portoneStatus = portonePayment.getStatus();
        if (!"paid".equals(portoneStatus)) {
            throw new GlobalException(PaymentErrorCode.PORTONE_VERIFICATION_FAILED);
        }

        //주문 정보 조회
        String merchantUid = portonePayment.getMerchantUid();
        Order order;
        try {
            order = orderQueryService.findOrderByCode(merchantUid);
        } catch (GlobalException ex) {
            throw new GlobalException(PaymentErrorCode.INVALID_MERCHANT_UID);
        }

        //결제 정보 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new GlobalException(AuthErrorCode.ACCESS_DENIED);
        }

        //결제 정보 조회
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new GlobalException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        //중복 결제 완료 방지
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new GlobalException(PaymentErrorCode.ALREADY_PAID_ORDER);
        }

        //금액 확인
        BigDecimal dbAmount = payment.getPrice();
        BigDecimal portoneAmount = portonePayment.getAmount(); // PortOne에 실제 결제된 금액

        if (dbAmount.compareTo(portoneAmount) != 0) {
            //금액 불일치시 환불 추가
            throw new GlobalException(PaymentErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 9. 모든 검증 통과, Service로 Payment 객체 반환
        return payment;
    }
}
