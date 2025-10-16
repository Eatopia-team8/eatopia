package org.example.eatopia.domain.category.dto.response;

import org.example.eatopia.domain.category.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryListResponse(
        Long id,
        String name,
        int depth,
        List<CategoryResponse> categoryList,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CategoryListResponse of(Category parent, List<CategoryResponse> children) {
        return new CategoryListResponse(
                parent.getId(),
                parent.getName(),
                parent.getDepth(),
                children,
                parent.getCreatedAt(),
                parent.getUpdatedAt()
        );
    }
}
