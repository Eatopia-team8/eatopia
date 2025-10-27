package org.example.eatopia.domain.payment.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.payment.exception.PaymentErrorCode;
import org.example.eatopia.domain.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentQueryServiceImpl implements PaymentQueryService {

    private final PaymentRepository paymentRepository;

    @Override
    public Payment getPaymentEntityByOrder(Order order) {
        return paymentRepository.findByOrder(order)
                .orElseThrow(() -> new GlobalException(PaymentErrorCode.PAYMENT_NOT_FOUND));
    }
}