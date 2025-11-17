package org.example.eatopia.domain.statistic.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.example.eatopia.domain.statistic.dto.request.SaleSearchRequest;
import org.example.eatopia.domain.statistic.dto.response.PeriodSaleResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleRankingResponse;
import org.example.eatopia.domain.statistic.dto.response.SaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.example.eatopia.domain.order.entity.QOrder.order;
import static org.example.eatopia.domain.order.entity.QOrderDetail.orderDetail;
import static org.example.eatopia.domain.product.entity.QProduct.product;
import static org.example.eatopia.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class StatisticRepositoryCustomImpl implements StatisticRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SaleResponse> findSellerSaleByPeriod(SaleSearchRequest condition, Pageable pageable) {
        StringTemplate periodFormat = getPeriodFormat(condition.period());
        LocalDateTime start = condition.getStartDateTime();
        LocalDateTime end = condition.getEndDateTime();

        List<SaleResponse> content = queryFactory
                .select(Projections.constructor(SaleResponse.class,
                        periodFormat.as("period"),
                        orderDetail.sellerId,
                        user.name.as("sellerName"),
                        orderDetail.price.multiply(orderDetail.quantity).sum().coalesce(BigDecimal.ZERO).as("totalSaleAmount")
                ))
                .from(orderDetail)
                .join(orderDetail.order, order)
                .join(orderDetail.product, product)
                .join(product.seller, user)
                .where(
                        order.status.eq(OrderStatus.SUCCESS),
                        createdAtBetween(start, end),
                        sellerIdEq(condition.sellerId())
                )
                .groupBy(periodFormat, orderDetail.sellerId, user.name)
                .orderBy(periodFormat.desc(), orderDetail.sellerId.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(orderDetail.sellerId)
                .from(orderDetail)
                .join(orderDetail.order, order)
                .join(orderDetail.product, product)
                .join(product.seller, user)
                .where(
                        order.status.eq(OrderStatus.SUCCESS),
                        createdAtBetween(start, end),
                        sellerIdEq(condition.sellerId())
                )
                .groupBy(periodFormat, orderDetail.sellerId);

        // PageableExecutionUtils.getPage 사용해 count 쿼리 최적화
        // fetch().size()를 사용하여 그룹의 총 개수를 계산합니다.
        return PageableExecutionUtils.getPage(content, pageable, () -> (long) countQuery.fetch().size());
    }

    @Override
    public List<PeriodSaleResponse> findTotalSaleByPeriod(SaleSearchRequest condition) {
        StringTemplate periodFormat = getPeriodFormat(condition.period());
        LocalDateTime start = condition.getStartDateTime();
        LocalDateTime end = condition.getEndDateTime();

        return queryFactory
                .select(Projections.constructor(PeriodSaleResponse.class,
                        periodFormat.as("period"),
                        orderDetail.price.multiply(orderDetail.quantity).sum().coalesce(BigDecimal.ZERO).as("totalAmount")
                ))
                .from(orderDetail)
                .join(orderDetail.order, order)
                .where(
                        order.status.eq(OrderStatus.SUCCESS),
                        createdAtBetween(start, end)
                )
                .groupBy(periodFormat)
                .orderBy(periodFormat.desc())
                .fetch();
    }

    @Override
    public List<SaleRankingResponse> findTopSellingSeller(SaleSearchRequest condition, int limit) {
        LocalDateTime start = condition.getStartDateTime();
        LocalDateTime end = condition.getEndDateTime();

        return queryFactory
                .select(Projections.constructor(SaleRankingResponse.class,
                        orderDetail.sellerId,
                        user.name.as("sellerName"),
                        orderDetail.price.multiply(orderDetail.quantity).sum().coalesce(BigDecimal.ZERO).as("totalAmount")
                ))
                .from(orderDetail)
                .join(orderDetail.order, order)
                .join(orderDetail.product, product)
                .join(product.seller, user)
                .where(
                        order.status.eq(OrderStatus.SUCCESS),
                        createdAtBetween(start, end)
                )
                .groupBy(orderDetail.sellerId, user.name)
                .orderBy(Expressions.numberPath(BigDecimal.class, "totalAmount").desc())
                .limit(limit)
                .fetch();
    }

    private StringTemplate getPeriodFormat(String periodType) {
        if ("monthly".equalsIgnoreCase(periodType)) {
            return Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m')", order.createdAt);
        }
        return Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", order.createdAt);
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return order.createdAt.between(start, end);
        } else if (start != null) {
            return order.createdAt.goe(start);
        } else if (end != null) {
            return order.createdAt.loe(end);
        }
        return null;
    }

    private BooleanExpression sellerIdEq(Long sellerId) {
        return sellerId != null ? orderDetail.sellerId.eq(sellerId) : null;
    }
}