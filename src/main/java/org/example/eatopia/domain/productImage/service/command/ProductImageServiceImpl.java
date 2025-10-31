package org.example.eatopia.domain.productImage.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.infra.s3.S3Service;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.productImage.dto.request.ProductImageAddRequest;
import org.example.eatopia.domain.productImage.dto.response.ProductImageResponse;
import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.productImage.exception.ProductImageErrorCode;
import org.example.eatopia.domain.productImage.exception.ProductImageException;
import org.example.eatopia.domain.productImage.repository.ProductImageRepository;
import org.example.eatopia.domain.productImage.validator.ProductImageValidator;
import org.example.eatopia.domain.user.config.UserRole;
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
    private final S3Service s3Service;

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

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);

        ProductImage target = images.stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ProductImageException(ProductImageErrorCode.PRD_IMAGE_NOT_FOUND));

        productImageValidator.validateImageOrderUpdate(target, productId, newOrder, images.size());

        int oldOrder = target.getDisplayOrder();

        // 동일한 순서면 early return
        if (java.util.Objects.equals(newOrder, oldOrder)) {
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

    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public void updateThumbnail(Long productId, Long imageId, Long userId) {

        Product product = productQueryService.getProductOrElseThrow(productId);
        product.verifySeller(userId);

        ProductImage newThumbnail = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ProductImageException(ProductImageErrorCode.PRD_IMAGE_NOT_FOUND));

        if (!newThumbnail.getProduct().getId().equals(productId)) {
            throw new ProductImageException(ProductImageErrorCode.PRD_IMAGE_NOT_BELONG_TO_PRODUCT);
        }

        // 이미 대표 이미지면 return
        if (Boolean.TRUE.equals(newThumbnail.getIsThumbnail())) {
            return;
        }

        productImageRepository.findThumbnailByProductId(productId)
                .ifPresent(thumbnail -> thumbnail.updateThumbnailStatus(false));

        newThumbnail.updateThumbnailStatus(true);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public void deleteProductImage(Long productId, Long imageId, Long userId, UserRole userRole) {

        Product product = productQueryService.getProductOrElseThrow(productId);
        product.verifySellerOrAdmin(userId, userRole == UserRole.ADMIN);

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ProductException(ProductImageErrorCode.PRD_IMAGE_NOT_FOUND));

        if (!image.getProduct().getId().equals(productId)) {
            throw new ProductException(ProductImageErrorCode.PRD_IMAGE_NOT_BELONG_TO_PRODUCT);
        }

        long imageCount = productImageRepository.countByProductId(productId);
        if (imageCount <= 1) {
            throw new ProductException(ProductImageErrorCode.PRD_IMAGE_LAST_ONE);
        }

        // 대표이미지 삭제 시 다음 이미지를 대표이미지로 지정
        if (image.getIsThumbnail()) {
            ProductImage nextThumbnail = productImageRepository.findFirstByProductIdAndIdNotOrderByDisplayOrderAsc(
                    productId, imageId
            ).orElseThrow(() -> new ProductException(ProductImageErrorCode.PRD_IMAGE_NOT_FOUND));

            nextThumbnail.updateThumbnailStatus(true);
            productImageRepository.save(nextThumbnail);
        }

        // DB에서 먼저 삭제
        productImageRepository.delete(image);

        // S3에서 삭제
        s3Service.deleteFile(image.getImageUrl());
    }
}

