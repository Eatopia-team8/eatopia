package org.example.eatopia.domain.review.dto.response;

import org.example.eatopia.domain.review.entity.ReviewReport;

import java.time.LocalDateTime;

public record ReviewReportResponse(
        Long id,
        String name,
        String reason,
        LocalDateTime createdAt
) {

    public static ReviewReportResponse from(ReviewReport report) {
        return new ReviewReportResponse(
                report.getId(),
                report.getUser().getName(),
                report.getReason(),
                report.getCreatedAt()
        );
    }
}
