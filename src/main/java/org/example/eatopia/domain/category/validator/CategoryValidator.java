package org.example.eatopia.domain.category.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.request.CategoryUpdateRequest;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.category.exception.CategoryErrorCode;
import org.example.eatopia.domain.category.exception.CategoryException;
import org.example.eatopia.domain.category.repository.CategoryRepository;
import org.example.eatopia.domain.category.service.query.CategoryQueryService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryValidator {
    private final CategoryRepository categoryRepository;
    private final CategoryQueryService categoryQueryService;

    // 이름 중복 체크
    public void validateNameDuplication(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new CategoryException(CategoryErrorCode.CTG_NAME_ALREADY_EXIST);
        }
    }

    // 생성 검증
    public Category validateCreateRequest(CategoryCreateRequest request) {
        // 이름 중복 체크
        validateNameDuplication(request.name());

        // 상위 카테고리 유효성 체크
        if (request.parentId() != null) {
            // 부모 ID가 있다면, 해당 ID를 가진 카테고리가 존재하는지 확인 (없으면 예외)
            Category parent = categoryQueryService.getCategoryOrElseThrow(request.parentId());

            // depth2에 하위 카테고리 추가 금지 (새로운 카테고리는 depth3이 될 수 없다)
            if (parent.getDepth() >= 2) {
                throw new CategoryException(CategoryErrorCode.CTG_INVALID_PARENT_DEPTH);
            }
            return parent;
        }

        return null;
    }

    // 수정 검증
    public Category validateUpdateRequest(Category currentCategory, CategoryUpdateRequest request) {
        // 이름이 바뀌었고 null이 아니면서 기존 이름과 다를 경우에만 체크
        if (request.name() != null && !request.name().equals(currentCategory.getName())) {
            validateNameDuplication(request.name());
        }

        // 상위 카테고리 변경 시 유효성 체크
        if (request.parentId() != null) {
            Category newParent = categoryQueryService.getCategoryOrElseThrow(request.parentId());

            // 자기 자신을 부모로 설정하는 것 방지
            if (newParent.getId().equals(currentCategory.getId())) {
                throw new CategoryException(CategoryErrorCode.CTG_INVALID_PARENT);
            }

            // depth1 카테고리는 parentId 수정 불가 (depth1이 다른 부모를 가질 수 없음)
            if (currentCategory.getDepth() == 1) {
                throw new CategoryException(CategoryErrorCode.CTG_DEPTH1_CANNOT_HAVE_PARENT);
            }

            // depth2에 하위 카테고리 추가 금지
            if (newParent.getDepth() >= 2) {
                throw new CategoryException(CategoryErrorCode.CTG_INVALID_PARENT_DEPTH);
            }

            return newParent;
        }

        // parentId를 null로 변경 시 체크
        if (currentCategory.getParent() != null) {
            // parentId가 null이 되게 하는 거 방지 (depth2는 depth1이 될 수 없다)
            if (currentCategory.getDepth() > 1) {
                throw new CategoryException(CategoryErrorCode.CTG_CANNOT_REMOVE_PARENT);
            }
        }

        return null;
    }
}