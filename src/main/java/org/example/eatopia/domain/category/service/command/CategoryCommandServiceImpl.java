package org.example.eatopia.domain.category.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;
import org.example.eatopia.domain.category.entity.Category;
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
}
