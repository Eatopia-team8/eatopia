package org.example.eatopia.domain.review.dto.request;

public record ReviewSearchCondition(
        String keyword,
        Integer rating
) {
}
