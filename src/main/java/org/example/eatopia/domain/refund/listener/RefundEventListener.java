package org.example.eatopia.domain.refund.listener;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.refund.dto.event.RefundSuccessEvent;
import org.example.eatopia.domain.refund.entity.Refund;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundEventListener {

    private final IamportClient iamportClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRefundSuccess(RefundSuccessEvent event) {
        Refund refund = event.refund();
        Payment payment = refund.getPayment();

        try {
            CancelData cancelData = new CancelData(payment.getImpUid(), true, refund.getAmount());
            cancelData.setReason(refund.getReason().name());

            iamportClient.cancelPaymentByImpUid(cancelData);

        } catch (Exception e) {
            log.error("PortOne API 환불 처리 실패! [Refund ID: {}, ImpUid: {}] - 오류: {}",
                    refund.getId(),
                    payment.getImpUid(),
                    e.getMessage(),
                    e
            );
        }
    }
}