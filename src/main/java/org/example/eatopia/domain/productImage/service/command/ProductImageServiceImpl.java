package org.example.eatopia.domain.productImage.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.productImage.dto.request.ProductImageAddRequest;
import org.example.eatopia.domain.productImage.dto.response.ProductImageResponse;
import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.productImage.exception.ProductImageErrorCode;
import org.example.eatopia.domain.productImage.exception.ProductImageException;
import org.example.eatopia.domain.productImage.repository.ProductImageRepository;
import org.example.eatopia.domain.productImage.validator.ProductImageValidator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductQueryService productQueryService;
    private final ProductImageValidator productImageValidator;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public ProductImageResponse addProductImage(Long productId, ProductImageAddRequest request, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);
        product.verifySeller(userId);

        long imageCount = productImageRepository.countByProductId(productId);
        productImageValidator.validateImageCount(imageCount);

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

    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public void updateImageOrder(Long productId, Long imageId, Integer newOrder, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);
        product.verifySeller(userId);

        List<ProductImage> images = productImageRepository.findAllByProductIdOrderByDisplayOrder(productId);

        ProductImage target = images.stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ProductImageException(ProductImageErrorCode.PRD_IMAGE_NOT_FOUND));

        productImageValidator.validateImageOrderUpdate(target, productId, newOrder, images.size());

        int oldOrder = target.getDisplayOrder();

        // 동일한 순서면 early return
        if (newOrder == oldOrder) {
            return;
        }

        // 순서를 올리거나 내리는 경우에 따라 다른 조정
        if (newOrder < oldOrder) {
            // 위로 올리는 경우 - newOrder ~ oldOrder-1 범위를 +1씩 뒤로 민다
            images.stream()
                    .filter(img -> img.getDisplayOrder() >= newOrder && img.getDisplayOrder() < oldOrder)
                    .forEach(img -> img.updateDisplayOrder(img.getDisplayOrder() + 1));
        } else {
            // 아래로 내리는 경우 -  oldOrder+1 ~ newOrder 범위를 -1씩 당긴다
            images.stream()
                    .filter(img -> img.getDisplayOrder() <= newOrder && img.getDisplayOrder() > oldOrder)
                    .forEach(img -> img.updateDisplayOrder(img.getDisplayOrder() - 1));
        }

        // 대상 이미지의 순서를 새로 설정
        target.updateDisplayOrder(newOrder);
    }
}

