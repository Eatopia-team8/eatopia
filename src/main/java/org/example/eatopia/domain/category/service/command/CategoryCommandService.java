package org.example.eatopia.domain.category.service.command;

import org.example.eatopia.domain.category.dto.request.CategoryCreateRequest;
import org.example.eatopia.domain.category.dto.response.CategoryResponse;

public interface CategoryCommandService {

    CategoryResponse createCategory(CategoryCreateRequest request);
}
