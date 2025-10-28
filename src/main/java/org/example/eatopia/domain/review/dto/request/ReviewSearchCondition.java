package org.example.eatopia.domain.review.dto.request;

import org.example.eatopia.domain.review.enums.ReviewStatus;

public record ReviewSearchCondition(
        String keyword,
        Integer rating,
        ReviewStatus status,
        Boolean includeDeleted
) {
}
