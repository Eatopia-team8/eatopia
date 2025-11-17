package org.example.eatopia.domain.product.service.command;

import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.request.ProductUpdateRequest;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.user.config.UserRole;

public interface ProductCommandService {

    ProductResponse createProduct(ProductCreateRequest request, Long userId);

    ProductResponse updateProduct(Long productId, ProductUpdateRequest request, Long userId);

    void deleteProduct(Long productId, Long userId, UserRole userRole);

    void decreaseStock(Long productId, Integer quantity);

    void increaseStock(Long productId, Integer quantity);
}
