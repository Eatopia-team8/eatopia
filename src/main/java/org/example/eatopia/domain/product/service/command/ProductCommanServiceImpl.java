package org.example.eatopia.domain.product.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.category.repository.CategoryRepository;
import org.example.eatopia.domain.category.service.query.CategoryQueryService;
import org.example.eatopia.domain.product.dto.request.ProductCreateRequest;
import org.example.eatopia.domain.product.dto.response.ProductResponse;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.exception.ProductErrorCode;
import org.example.eatopia.domain.product.exception.ProductException;
import org.example.eatopia.domain.product.repository.ProductRepository;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCommanServiceImpl implements ProductCommandService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryQueryService categoryQueryService;
    private final ProductQueryService productQueryService;

    // 상품 등록
    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        // 가격 검증
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new ProductException(ProductErrorCode.PRD_INVALID_PRICE);
        }

        // 재고 검증
        if (request.stock() == null || request.stock() < 0) {
            throw new ProductException(ProductErrorCode.PRD_INVALID_STOCK);
        }

        // 카테고리 조회
        Category category = null;
        if (request.categoryId() != null) {
            category = categoryQueryService.getCategoryOrElseThrow(request.categoryId());

            // 상위 카테고리 상품 등록 불가
            if (category.getParent() == null) {
                throw new ProductException(ProductErrorCode.PRD_INVALID_CATEGORY);
            }
        }

        Product product = Product.create(
                request.name(),
                request.description(),
                request.thumbnailUrl(),
                request.price(),
                request.stock(),
                request.status(),
                category
        );

        product = productRepository.save(product);

        return ProductResponse.from(product);
    }
}
