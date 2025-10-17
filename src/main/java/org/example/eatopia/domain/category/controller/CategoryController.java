package org.example.eatopia.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;
import org.example.eatopia.domain.category.service.command.CategoryCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryCommandService categoryCommandService;

    // 카테고리 생성
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/v1/categories")
    public ResponseEntity<Response<CategoryResponse>> createCategory(@RequestBody CategoryCreateRequest request) {

        CategoryResponse response = categoryCommandService.createCategory(request);

        return ResponseEntity.ok(Response.success(response));
    }
}
