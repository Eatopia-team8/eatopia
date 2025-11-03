package org.example.eatopia.domain.product.service.query;

import org.example.eatopia.domain.product.dto.request.ProductSearchCondition;
import org.example.eatopia.domain.product.enums.ProductSortBy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("cacheConditionHelper")
public class CacheConditionHelper {

    /**
     * V3 상품 목록 캐시 가능 여부 확인
     * - categoryId가 존재하고
     * - sortBy가 LATEST이거나 null (최신순)
     * - 페이지 번호가 10 이하
     * - 다른 필터(keyword, status, price)가 없을 때만 캐시
     */
    public boolean isCacheableForProductListV3(ProductSearchCondition condition, Pageable pageable) {
        // 필터가 없는지 확인
        boolean hasNoFilters = !StringUtils.hasText(condition.keyword())
                && !StringUtils.hasText(condition.status())
                && condition.minPrice() == null
                && condition.maxPrice() == null;

        // 캐시 조건
        return condition.categoryId() != null
                && (condition.sortBy() == null || condition.sortBy() == ProductSortBy.LATEST)
                && pageable.getPageNumber() < 10
                && hasNoFilters;
    }
}