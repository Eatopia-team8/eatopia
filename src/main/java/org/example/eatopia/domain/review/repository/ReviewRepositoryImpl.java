package org.example.eatopia.domain.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.example.eatopia.domain.review.entity.QReview.review;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewSearchResponse> searchReviewsByProduct(Long productId, ReviewSearchCondition condition, Pageable pageable) {

        BooleanBuilder filter = buildFilter(productId, condition);

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(pageable.getSort());

        List<ReviewSearchResponse> content = queryFactory
                .select(
                        Projections.constructor(
                                ReviewSearchResponse.class,
                                review.id,
                                review.user.name,
                                review.content,
                                review.rating,
                                review.createdAt
                        )
                )
                .from(review)
                .where(filter)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(filter);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder buildFilter(Long productId, ReviewSearchCondition condition) {

        BooleanBuilder builder = new BooleanBuilder();

        // 특정 상품에 대한 리뷰
        builder.and(review.product.id.eq(productId));

        // 활성화 상태인 리뷰만 노출
        builder.and(review.status.eq(ReviewStatus.ACTIVE));

        // 내용 검색
        if (StringUtils.hasText(condition.keyword())) {
            builder.and(review.content.contains(condition.keyword()));
        }

        // 별점
        if (condition.rating() != null) {
            builder.and(review.rating.eq(condition.rating()));
        }

        return builder;
    }

    private OrderSpecifier<?> getOrderSpecifier(Sort sort) {

        Sort.Order order = sort.iterator().next();
        String property = order.getProperty();
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;

        return switch (property) {
            case "rating" -> new OrderSpecifier<>(direction, review.rating);
            case "createdAt" -> new OrderSpecifier<>(direction, review.createdAt);
            default -> new OrderSpecifier<>(Order.DESC, review.createdAt);
        };
    }

}
