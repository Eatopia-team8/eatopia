package org.example.eatopia.domain.review.dto.response;

import java.time.LocalDateTime;

public record ReviewReportResponse(
        Long id,
        String name,
        String reason,
        LocalDateTime createdAt
) {
}
