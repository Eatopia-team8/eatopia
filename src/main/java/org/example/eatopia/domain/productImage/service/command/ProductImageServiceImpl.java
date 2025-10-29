package org.example.eatopia.domain.productImage.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.productImage.dto.request.ProductImageAddRequest;
import org.example.eatopia.domain.productImage.dto.response.ProductImageResponse;
import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.productImage.exception.ProductImageErrorCode;
import org.example.eatopia.domain.productImage.repository.ProductImageRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductQueryService productQueryService;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public ProductImageResponse addProductImage(Long productId, ProductImageAddRequest request, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);
        product.verifySeller(userId);

        long imageCount = productImageRepository.countByProductId(productId);
        if (imageCount >= 10) {
            throw new ProductException(ProductImageErrorCode.PRD_IMAGE_EXCEED_LIMIT);
        }

        if (Boolean.TRUE.equals(request.isThumbnail())) {
            productImageRepository.findThumbnailByProductId(productId)
                    .ifPresent(thumbnail -> thumbnail.updateThumbnailStatus(false));
        }

        ProductImage image = ProductImage.create(
                product,
                request.imageUrl(),
                request.displayOrder(),
                request.isThumbnail()
        );

        image = productImageRepository.save(image);
        return ProductImageResponse.from(image);
    }
}