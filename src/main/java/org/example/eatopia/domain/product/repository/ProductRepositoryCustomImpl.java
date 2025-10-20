package org.example.eatopia.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
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

        // 공통 검색 조건 생성
        BooleanBuilder searchConditions = buildSearchConditions(condition, categoryIds);

        // 상품 목록 조회
        List<Product> content = queryFactory
                .selectFrom(product)
                .leftJoin(product.category, category).fetchJoin()
                .leftJoin(product.seller).fetchJoin()
                .where(searchConditions)
                .orderBy(product.createdAt.desc()) // 신상품 정렬 기준
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(searchConditions);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 상품 검색 통합해서 BooleanBuilder 생성
    private BooleanBuilder buildSearchConditions(ProductSearchCondition condition, List<Long> categoryIds) {
        BooleanBuilder builder = new BooleanBuilder();

        // HIDE 상태는 기본적으로 제외 (필수 조건)
        builder.and(statusNotHide());

        // 선택적 조건들
        appendKeywordCondition(builder, condition.keyword());
        appendCategoryCondition(builder, categoryIds);
        appendStatusCondition(builder, condition.status());
        appendPriceCondition(builder, condition.minPrice(), condition.maxPrice());

        return builder;
    }

    // 키워드 검색 (상품명 or 설명에 포함)
    private void appendKeywordCondition(BooleanBuilder builder, String keyword) {
        if (StringUtils.hasText(keyword)) {
            builder.and(product.name.containsIgnoreCase(keyword)
                    .or(product.description.containsIgnoreCase(keyword)));
        }
    }

    // 카테고리 필터링
    private void appendCategoryCondition(BooleanBuilder builder, List<Long> categoryIds) {
        if (categoryIds != null && !categoryIds.isEmpty()) {
            builder.and(product.category.id.in(categoryIds));
        }
    }

    // 상태 필터링
    private void appendStatusCondition(BooleanBuilder builder, String status) {
        if (StringUtils.hasText(status)) {
            try {
                ProductStatus productStatus = ProductStatus.valueOf(status.toUpperCase());
                builder.and(product.status.eq(productStatus));
            } catch (IllegalArgumentException e) {
                // 유효하지 않은 상태는 무시
            }
        }
    }

    // 가격 범위 필터
    private void appendPriceCondition(BooleanBuilder builder, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null) {
            builder.and(product.price.goe(minPrice));
        }
        if (maxPrice != null) {
            builder.and(product.price.loe(maxPrice));
        }
    }
    
    // HIDE 상태 제외
    private BooleanExpression statusNotHide() {
        return product.status.ne(ProductStatus.HIDE);
    }
}
