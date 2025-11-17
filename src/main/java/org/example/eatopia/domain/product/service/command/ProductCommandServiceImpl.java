package org.example.eatopia.domain.product.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.category.service.query.CategoryQueryService;
import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.request.ProductUpdateRequest;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.exception.ProductErrorCode;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.product.repository.ProductRepository;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.product.validator.ProductValidator;
import org.example.eatopia.domain.productImage.dto.request.ProductImageInfo;
import org.example.eatopia.domain.productImage.entity.ProductImage;
import org.example.eatopia.domain.productImage.repository.ProductImageRepository;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryQueryService categoryQueryService;
    private final ProductValidator productValidator;
    private final UserQueryService userQueryService;
    private final ProductQueryService productQueryService;

    // 상품 등록
    @Override
    @CacheEvict(value = "productList", allEntries = true)
    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        productValidator.validateCreateRequest(request);

        // 카테고리 조회
        Category category = categoryQueryService.getCategoryOrElseThrow(request.categoryId());
        productValidator.validateCategory(category);

        User seller = userQueryService.getUserEntityById(userId);

        Product product = Product.create(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.status(),
                category,
                seller
        );

        productRepository.save(product);

        List<ProductImage> images = saveProductImages(product, request.images());

        return ProductResponse.of(product, images);
    }

    // 상품 수정 (이미지 제외)
    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request, Long userId) {

        // 수정할 항목이 하나라도 있는지 확인
        if (request.hasNoUpdate()) {
            throw new ProductException(ProductErrorCode.PRD_NO_UPDATE_FIELDS);
        }

        Product product = productQueryService.getProductOrElseThrow(productId);
        product.verifySeller(userId);

        productValidator.validateUpdateRequest(request);

        // 카테고리 변경
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryQueryService.getCategoryOrElseThrow(request.categoryId());
            productValidator.validateCategory(category);
        }

        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.status(),
                category
        );

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);

        return ProductResponse.of(product, images);
    }

    // 상품 삭제
    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public void deleteProduct(Long productId, Long userId, UserRole userRole) {

        userQueryService.getUserEntityById(userId);

        Product product = productQueryService.getProductOrElseThrow(productId);

        product.verifySellerOrAdmin(userId, userRole == UserRole.ADMIN);

        productRepository.delete(product);
    }

    // 재고 감소
    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = findProductWithLock(productId);

        product.decreaseStock(quantity);
    }

    // 결제 취소시 재고 롤백
    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "productList", allEntries = true)
    })
    public void increaseStock(Long productId, Integer quantity) {
        Product product = findProductWithLock(productId);

        product.increaseStock(quantity);
    }

    private Product findProductWithLock(Long productId) {
        return productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRD_NOT_FOUND));
    }

    // 이미지 저장 (생성)
    private List<ProductImage> saveProductImages(Product product, List<ProductImageInfo> imageInfos) {
        List<ProductImage> images = imageInfos.stream()
                .map(imageInfo -> ProductImage.create(
                        product,
                        imageInfo.imageUrl(),
                        imageInfo.displayOrder(),
                        imageInfo.isThumbnail()
                ))
                .toList();

        return productImageRepository.saveAll(images);
    }
}
