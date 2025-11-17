package org.example.eatopia.domain.category.dto.request;

public record CategoryUpdateRequest(
        String name,
        Long parentId
) {
}
