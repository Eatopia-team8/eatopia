package org.example.eatopia.domain.category.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.request.CategoryUpdateRequest;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.category.exception.CategoryErrorCode;
import org.example.eatopia.domain.category.exception.CategoryException;
import org.example.eatopia.domain.category.repository.CategoryRepository;
import org.example.eatopia.domain.category.service.query.CategoryQueryService;
import org.example.eatopia.domain.category.validator.CategoryValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandServiceImpl implements CategoryCommandService {

    private final CategoryRepository categoryRepository;
    private final CategoryQueryService categoryQueryService;
    private final CategoryValidator categoryValidator;

    /**
     * 카테고리 생성
     * 상위 카테고리 ID가 있을 시 하위 카테고리 없을 시 상위 카테고리
     */
    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {

        Category parent = categoryValidator.validateCreateRequest(request);

        Category newCategory = Category.create(
                request.name(),
                parent
        );

        newCategory = categoryRepository.save(newCategory);

        return CategoryResponse.from(newCategory);
    }

    /**
     * 카테고리 수정
     * depth1 카테고리는 parentId 수정 불가
     * depth2 카테고리는 parentId 수정 가능 (다른 카테고리로 이동 가능)
     */
    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        // 수정 대상 확인
        Category category = categoryQueryService.getCategoryOrElseThrow(categoryId);

        Category newParent = categoryValidator.validateUpdateRequest(category, request);

        category.update(request.name(), newParent);

        return CategoryResponse.from(category);
    }

    // 카테고리 삭제
    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryQueryService.getCategoryOrElseThrow(categoryId);

        // 하위 카테고리가 있는지 확인
        if (categoryRepository.existsByParentId(categoryId)) {
            throw new CategoryException(CategoryErrorCode.CTG_HAS_CHILDREN);
        }

        categoryRepository.delete(category);
    }
}
