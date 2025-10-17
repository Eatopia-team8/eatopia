package org.example.eatopia.domain.category.dto.response;

import org.example.eatopia.domain.category.entity.Category;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        int depth,
        Long parentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CategoryResponse from(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDepth(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}