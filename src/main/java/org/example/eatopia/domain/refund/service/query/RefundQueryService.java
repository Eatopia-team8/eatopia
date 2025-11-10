package org.example.eatopia.domain.refund.service.query;

import org.example.eatopia.domain.refund.dto.response.RefundResponse;
import org.example.eatopia.domain.refund.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RefundQueryService {

    Page<RefundResponse> getRefunds(Long userId, Pageable pageable);

    BigDecimal getSuccessAmountByPaymentId(Long paymentId);

    List<Refund> getUnsettleRefunds(Long sellerId);
}
