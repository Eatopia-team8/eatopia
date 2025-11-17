package org.example.eatopia.domain.refund.service.command;

import org.example.eatopia.domain.refund.dto.request.RefundCreateRequest;
import org.example.eatopia.domain.refund.dto.response.RefundResponse;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.settlement.entity.Settlement;

import java.util.List;

public interface RefundCommandService {
    //환불 대기
    RefundResponse requestRefund(Long userId, RefundCreateRequest request);

    //환불 승인
    RefundResponse successRefund(Long refundId);

    //환불 거절
    RefundResponse canceledRefund(Long refundId);

    //환불 실패
    void failRefund(Long refundId, String reason);

    void settlementToRefunds(List<Long> refundIds, Settlement settlement);

    void rollbackSettlementForRefunds(List<Refund> refunds);
}