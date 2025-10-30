package org.example.eatopia.domain.review.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewAdminResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSellerResponse;
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

        return (Page<ReviewSearchResponse>) createPage(content, pageable, filter);
    }

    @Override
    public Page<ReviewSellerResponse> getReviewsForSeller(Long productId, Long sellerId, ReviewSearchCondition condition, Pageable pageable) {

        BooleanBuilder filter = buildSellerFilter(productId, sellerId, condition);

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(pageable.getSort());

        List<ReviewSellerResponse> content = queryFactory
                .select(
                        Projections.constructor(
                                ReviewSellerResponse.class,
                                review.id,
                                review.product.id,
                                review.product.name,
                                review.user.name,
                                review.content,
                                review.rating,
                                review.status,
                                review.createdAt,
                                review.updatedAt,
                                review.reportedAt,
                                review.deletedAt
                        )
                )
                .from(review)
                .where(filter)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return (Page<ReviewSellerResponse>) createPage(content, pageable, filter);
    }

    @Override
    public Page<ReviewAdminResponse> getReviewsForAdmin(Long productId, Long userId, ReviewSearchCondition condition, Pageable pageable) {

        BooleanBuilder filter = buildAdminFilter(productId, userId, condition);

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(pageable.getSort());

        List<ReviewAdminResponse> content = queryFactory
                .select(
                        Projections.constructor(
                                ReviewAdminResponse.class,
                                review.id,
                                review.user.id,
                                review.user.name,
                                review.product.id,
                                review.product.name,
                                review.content,
                                review.rating,
                                review.status,
                                review.reportedAt,
                                review.reportCount,
                                review.handledById,
                                review.createdAt,
                                review.updatedAt,
                                review.deletedAt
                        )
                )
                .from(review)
                .where(filter)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return (Page<ReviewAdminResponse>) createPage(content, pageable, filter);
    }

    private Page<?> createPage(List<?> content, Pageable pageable, BooleanBuilder filter) {
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(filter);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder buildFilter(Long productId, ReviewSearchCondition condition) {

        return new BooleanBuilder()
                .and(productFilter(productId))
                .and(keywordFilter(condition.keyword()))
                .and(ratingFilter(condition.rating()))
                .and(review.status.in(ReviewStatus.ACTIVE, ReviewStatus.REPORTED))
                .and(review.deletedAt.isNull());
    }

    private BooleanBuilder buildSellerFilter(Long productId, Long sellerId, ReviewSearchCondition condition) {

        return new BooleanBuilder()
                .and(review.product.seller.id.eq(sellerId))
                .and(productFilter(productId))
                .and(keywordFilter(condition.keyword()))
                .and(ratingFilter(condition.rating()))
                .and(statusFilter(condition.status()))
                .and(includeDeletedFilter(condition.includeDeleted()));
    }

    private BooleanBuilder buildAdminFilter(Long productId, Long userId, ReviewSearchCondition condition) {

        return new BooleanBuilder()
                .and(productFilter(productId))
                .and(keywordFilter(condition.keyword()))
                .and(ratingFilter(condition.rating()))
                .and(statusFilter(condition.status()))
                .and(includeDeletedFilter(condition.includeDeleted()))
                .and(onlyReportedFilter(condition.onlyReported()))
                .and(userFilter(userId));
    }

    // 상품 필터
    private BooleanExpression productFilter(Long productId) {
        return productId != null ? review.product.id.eq(productId) : null;
    }

    // 키워드 필터
    private BooleanExpression keywordFilter(String keyword) {
        return StringUtils.hasText(keyword) ? review.content.contains(keyword) : null;
    }

    // 별점 필터
    private BooleanExpression ratingFilter(Integer rating) {
        return rating != null ? review.rating.eq(rating) : null;
    }

    // 상태 필터
    private BooleanExpression statusFilter(ReviewStatus status) {
        return status != null ? review.status.eq(status) : null;
    }

    // 삭제 포함 필터
    private BooleanExpression includeDeletedFilter(Boolean includeDeleted) {
        return Boolean.FALSE.equals(includeDeleted) ? review.deletedAt.isNull() : null;
    }

    // 신고 리뷰만 필터
    private BooleanExpression onlyReportedFilter(Boolean onlyReported) {
        return Boolean.TRUE.equals(onlyReported) ? review.reportedAt.isNotNull() : null;
    }

    // 회원 필터
    private BooleanExpression userFilter(Long userId) {
        return userId != null ? review.user.id.eq(userId) : null;
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
