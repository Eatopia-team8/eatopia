package org.example.eatopia.domain.settlement.dto.response;

import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.enums.SettlementStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SettlementResponse(
        Long settlementId,

        Long sellerId,

        String sellerName,

        SettlementStatus status,

        BigDecimal finalSettlementAmount, // 최종 정산액

        LocalDateTime createdAt, // 정산 요청일

        LocalDateTime completedAt // 정산 완료일
) {
    public static SettlementResponse from(Settlement s) {
        return new SettlementResponse(
                s.getId(),
                s.getSeller().getId(),
                s.getSeller().getName(),
                s.getStatus(),
                s.getFinalSettlementAmount(),
                s.getCreatedAt(),
                s.getCompletedAt()
        );
    }
}