package org.example.eatopia.domain.category.dto.request;

public record CategoryCreateRequest(
        String name,
        Long parentId
) {
}