package org.example.eatopia.domain.category.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.dto.response.CategoryListResponse;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.category.exception.CategoryErrorCode;
import org.example.eatopia.domain.category.exception.CategoryException;
import org.example.eatopia.domain.category.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;

    // 유저용 전체 카테고리 조회 (페이징 없음)
    public List<CategoryListResponse> getAllCategories() {
        List<Category> allCategories = categoryRepository.findAllByOrderById();

        if (allCategories.isEmpty()) {
            return List.of();
        }

        List<Category> parents = allCategories.stream()
                .filter(c -> c.getParent() == null)
                .toList();

        List<Category> children = allCategories.stream()
                .filter(c -> c.getParent() != null)
                .toList();

        return buildHierarchy(parents, children);
    }

    // 관리자용 카테고리 페이징 조회
    public Page<CategoryListResponse> getCategoriesPaged(Pageable pageable) {
        Page<Category> parentPage = categoryRepository.findParentCategories(pageable);

        if (parentPage.isEmpty()) {
            return Page.empty();
        }

        List<Long> parentIds = parentPage.getContent().stream()
                .map(Category::getId)
                .toList();

        List<Category> children = categoryRepository.findByParentIdIn(parentIds);

        List<CategoryListResponse> content = buildHierarchy(parentPage.getContent(), children);

        return new PageImpl<>(content, pageable, parentPage.getTotalElements());
    }

    /**
     * 상위 카테고리 목록, 전체 하위 카테고리 목록 조립
     *
     * @param parents  상위 카테고리 목록
     * @param children 상위 카테고리들에 속한 모든 하위 카테고리 목록
     * @return CategoryListResponse 목록
     */
    private List<CategoryListResponse> buildHierarchy(List<Category> parents, List<Category> children) {
        // 하위 카테고리 그룹
        Map<Long, List<CategoryResponse>> childrenMap = children.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.groupingBy(CategoryResponse::parentId));

        // 상위 카테고리 + 하위 카테고리 그룹
        return parents.stream()
                .map(parent -> CategoryListResponse.of(
                        parent,
                        childrenMap.getOrDefault(parent.getId(), List.of())
                ))
                .toList();
    }

    // 카테고리 ID로 조회 없으면 예외
    @Override
    public Category getCategoryOrElseThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CTG_ID_NOT_FOUND));
    }
}
