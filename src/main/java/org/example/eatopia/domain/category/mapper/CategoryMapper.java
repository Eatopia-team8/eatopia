package org.example.eatopia.domain.category.mapper;

import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.response.CategoryListResponse;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;
import org.example.eatopia.domain.category.entity.Category;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {
    }

    // Request -> Entity
    public static Category toEntity(Category parent, CategoryCreateRequest request) {
        return Category.builder()
                .name(request.name())
                .depth(request.depth() != null ? request.depth() : (parent == null ? 1 : parent.getDepth() + 1))
                .parent(parent)
                .build();
    }

    // Entity -> Response
    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.from(category);
    }

    // Entity + 하위 목록 → ListResponse
    public static CategoryListResponse toListResponse(Category parent, List<Category> children) {
        return CategoryListResponse.of(
                parent,
                children.stream()
                        .map(CategoryResponse::from)
                        .toList()
        );
    }
}