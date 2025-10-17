package org.example.eatopia.domain.category.validator;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
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
}