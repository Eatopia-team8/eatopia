package org.example.eatopia.domain.refund.service.query;

import org.example.eatopia.domain.refund.dto.response.RefundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface RefundQueryService {

    Page<RefundResponse> getRefunds(Long userId, Pageable pageable);

    BigDecimal getSuccessAmountByPaymentId(Long paymentId);
}
