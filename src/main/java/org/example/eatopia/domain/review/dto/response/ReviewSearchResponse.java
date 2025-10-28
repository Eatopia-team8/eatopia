package org.example.eatopia.domain.review.dto.response;

import java.time.LocalDateTime;

public record ReviewSearchResponse(
        Long id,
        String name,
        String content,
        int rating,
        LocalDateTime createdAt
) {
}
