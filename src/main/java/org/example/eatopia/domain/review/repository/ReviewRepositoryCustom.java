package org.example.eatopia.domain.review.repository;

import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    Page<ReviewSearchResponse> searchReviewsByProduct(Long productId, ReviewSearchCondition condition, Pageable pageable);
}
