package org.example.eatopia.domain.product.service.command;

import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.response.ProductResponse;

public interface ProductCommandService {

    ProductResponse createProduct(ProductCreateRequest request, Long userId);
}
