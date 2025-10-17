package org.example.eatopia.domain.category.service.query;

import org.example.eatopia.domain.category.dto.response.CategoryListResponse;
import org.example.eatopia.domain.category.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryQueryService {

    List<CategoryListResponse> getAllCategories();

    Page<CategoryListResponse> getCategoriesPaged(Pageable pageable);

    // 카테고리 ID로 조회 없으면 예외
    Category getCategoryOrElseThrow(Long categoryId);
}
