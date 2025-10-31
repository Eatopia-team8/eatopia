package org.example.eatopia.domain.payment.service.command;

import com.siot.IamportRestClient.exception.IamportResponseException;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.dto.request.PaymentCreateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentUpdateRequest;
import org.example.eatopia.domain.payment.dto.request.PaymentVerifyRequest;
import org.example.eatopia.domain.payment.dto.response.PaymentResponse;

import java.io.IOException;

public interface PaymentCommandService {

    PaymentResponse createPayment(Long userId, PaymentCreateRequest request);

    void cancelPaymentByOrder(Order order);

    PaymentResponse updatePaymentMethod(Long userId, Long paymentId, PaymentUpdateRequest request);

    /**
     * PortOne 결제검증 및 결제
     *
     * @throws IamportResponseException PortOne API 통신 오류
     * @throws IOException              네트워크 오류
     */
    PaymentResponse verifyPayment(Long userId, PaymentVerifyRequest request) throws IamportResponseException, IOException;

    //부분 환불 성공시 payment 갱신
    void partialRefund(Long paymentId);
}
