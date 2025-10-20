package org.example.eatopia.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.category.service.query.CategoryQueryService;
import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.example.eatopia.domain.category.entity.QCategory.category;
import static org.example.eatopia.domain.product.entity.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final CategoryQueryService categoryQueryService;

    @Override
    public Page<Product> searchProducts(ProductSearchCondition condition, Pageable pageable) {

        // 하위 카테고리 ID 목록 조회
        List<Long> categoryIds = categoryQueryService.getCategoryIdsWithChildren(condition.categoryId());

        // 상품 목록 조회
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .leftJoin(product.seller).fetchJoin()
                .where(
                        keywordContains(condition.keyword()),
                        categoryIdIn(categoryIds),
                        statusEq(condition.status()),
                        priceGoe(condition.minPrice()),
                        priceLoe(condition.maxPrice()),
                        statusNotHide() // HIDE 상태는 기본적으로 제외
                )
                .orderBy(product.createdAt.desc()) // 신상품 정렬 기준
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(
                        keywordContains(condition.keyword()),
                        categoryIdIn(categoryIds),
                        statusEq(condition.status()),
                        priceGoe(condition.minPrice()),
                        priceLoe(condition.maxPrice()),
                        statusNotHide()
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 키워드 검색 (상품명 or 설명에 포함)
    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return product.name.containsIgnoreCase(keyword)
                .or(product.description.containsIgnoreCase(keyword));
    }

    // 카테고리 ID 목록으로 필터링
    private BooleanExpression categoryIdIn(List<Long> categoryIds) {
        return categoryIds != null && !categoryIds.isEmpty()
                ? product.category.id.in(categoryIds)
                : null;
    }

    // 상태 필터링
    private BooleanExpression statusEq(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        try {
            ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
            return product.status.eq(productStatus);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // HIDE 상태 제외
    private BooleanExpression statusNotHide() {
        return product.status.ne(ProductStatus.HIDE);
    }

    // 최소 가격 이상
    private BooleanExpression priceGoe(BigDecimal minPrice) {
        return minPrice != null ? product.price.goe(minPrice) : null;
    }

    // 최대 가격 이하
    private BooleanExpression priceLoe(BigDecimal maxPrice) {
        return maxPrice != null ? product.price.loe(maxPrice) : null;
    }
}
