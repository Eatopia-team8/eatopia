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

    /**
     * 카테고리 ID에 따른 하위 카테고리 ID 목록 조회
     * - 부모 카테고리인 경우: 해당 부모를 가진 모든 자식 카테고리 ID 반환
     * - 자식 카테고리인 경우: 해당 카테고리 ID만 반환
     * - 존재하지 않는 카테고리: null 반환
     */
    List<Long> getCategoryIdsWithChildren(Long categoryId);
}
