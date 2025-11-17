package org.example.eatopia.domain.productImage.service.command;

import org.example.eatopia.domain.productImage.dto.request.ProductImageAddRequest;
import org.example.eatopia.domain.productImage.dto.response.ProductImageResponse;
import org.example.eatopia.domain.user.config.UserRole;

public interface ProductImageService {

    ProductImageResponse addProductImage(Long productId, ProductImageAddRequest request, Long userId);

    void updateImageOrder(Long productId, Long imageId, Integer newOrder, Long userId);

    void updateThumbnail(Long productId, Long imageId, Long userId);

    void deleteProductImage(Long productId, Long imageId, Long userId, UserRole userRole);
}
