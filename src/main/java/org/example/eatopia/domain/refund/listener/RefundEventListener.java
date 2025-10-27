package org.example.eatopia.domain.refund.listener;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.refund.dto.event.RefundSuccessEvent;
import org.example.eatopia.domain.refund.entity.Refund;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
            //validator 추가
        }
    }
}