package org.example.eatopia.domain.refund.service.command;

import org.example.eatopia.domain.refund.dto.request.RefundCreateRequest;
import org.example.eatopia.domain.refund.dto.response.RefundResponse;

public interface RefundCommandService {
    //환불 대기
    RefundResponse requestRefund(Long userId, RefundCreateRequest request);

    //환불 승인
    RefundResponse successRefund(Long refundId);

    //환불 거절
    RefundResponse canceledRefund(Long refundId);
}