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
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryQueryService categoryQueryService;
    private final ProductValidator productValidator;
    private final UserQueryService userQueryService;
    private final ProductQueryService productQueryService;

    // 상품 등록
    @Override
    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {

        productValidator.validateCreateRequest(request);

        // 카테고리 조회
        Category category = categoryQueryService.getCategoryOrElseThrow(request.categoryId());
        productValidator.validateCategory(category);

        User seller = userQueryService.getUserEntityById(userId);

        Product product = Product.create(
                request.name(),
                request.description(),
                request.thumbnailUrl(),
                request.price(),
                request.stock(),
                request.status(),
                category,
                seller
        );

        product = productRepository.save(product);

        return ProductResponse.from(product);
    }

    // 상품 수정
    @Override
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
                request.thumbnailUrl(),
                request.price(),
                request.stock(),
                request.status(),
                category
        );

        return ProductResponse.from(product);
    }

    // 상품 삭제
    @Override
    public void deleteProduct(Long productId, Long userId, UserRole userRole) {

        Product product = productQueryService.getProductOrElseThrow(productId);
        User user = userQueryService.getUserEntityById(userId);

        product.verifySellerOrAdmin(userId, userRole == UserRole.ADMIN);

        productRepository.delete(product);
    }

    // 재고 감소
    @Override
    public void decreaseStock(Long productId, Long quantity) {
        Product product = findProductWithLock(productId);

        product.decreaseStock(quantity);
    }

    // 결제 취소시 재고 롤백
    @Override
    public void increaseStock(Long productId, Long quantity) {
        Product product = findProductWithLock(productId);

        product.increaseStock(quantity);
    }

    private Product findProductWithLock(Long productId) {
        return productRepository.findByIdWithPessimisticLock(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRD_NOT_FOUND));
    }
}
