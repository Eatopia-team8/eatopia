package org.example.eatopia.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.request.CategoryUpdateRequest;
import org.example.eatopia.domain.category.dto.response.CategoryListResponse;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;
import org.example.eatopia.domain.category.service.command.CategoryCommandService;
import org.example.eatopia.domain.category.service.query.CategoryQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    // 카테고리 생성
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/v1/categories")
    public ResponseEntity<Response<CategoryResponse>> createCategory(@RequestBody CategoryCreateRequest request) {

        CategoryResponse response = categoryCommandService.createCategory(request);

        return ResponseEntity.ok(Response.success(response));
    }

    // 카테고리 수정
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/v1/categories/{categoryId}")
    public ResponseEntity<Response<CategoryResponse>> updateCategory(@PathVariable Long categoryId,
                                                                     @RequestBody CategoryUpdateRequest request) {

        CategoryResponse response = categoryCommandService.updateCategory(categoryId, request);

        return ResponseEntity.ok(Response.success(response));
    }

    // 카테고리 삭제
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/v1/categories/{categoryId}")
    public ResponseEntity<Response<Void>> deleteCategory(@PathVariable Long categoryId) {

        categoryCommandService.deleteCategory(categoryId);

        return ResponseEntity.ok(Response.success(null));
    }

    // 카테고리 전체 조회 (구매자/판매자용)
    @GetMapping("/v1/categories")
    public ResponseEntity<Response<List<CategoryListResponse>>> getCategories() {

        List<CategoryListResponse> categories = categoryQueryService.getAllCategories();

        return ResponseEntity.ok(Response.success(categories));
    }

    // 카테고리 페이징 조회 (관리자용)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/v1/admin/categories")
    public ResponseEntity<Response<Page<CategoryListResponse>>> getAdminCategories(
            @PageableDefault(sort = "id", size = 10) Pageable pageable
    ) {

        Page<CategoryListResponse> result = categoryQueryService.getCategoriesPaged(pageable);

        return ResponseEntity.ok(Response.success(result));
    }
}
