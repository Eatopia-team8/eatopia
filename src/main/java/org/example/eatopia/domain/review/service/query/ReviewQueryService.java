package org.example.eatopia.domain.review.service.query;

import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewAdminResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSellerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewQueryService {

    Page<ReviewSearchResponse> searchReviews(Long productId, ReviewSearchCondition condition, Pageable pageable);

    Page<ReviewSellerResponse> getReviewsForSeller(Long productId, Long sellerId, ReviewSearchCondition condition, Pageable pageable);

    Page<ReviewAdminResponse> getReviewsForAdmin(Long productId, Long userId, ReviewSearchCondition condition, Pageable pageable);
}
