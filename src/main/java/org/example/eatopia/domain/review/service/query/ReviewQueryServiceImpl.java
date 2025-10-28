package org.example.eatopia.domain.review.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSellerResponse;
import org.example.eatopia.domain.review.enums.ReviewStatus;
import org.example.eatopia.domain.review.exception.ReviewErrorCode;
import org.example.eatopia.domain.review.repository.ReviewReportRepository;
import org.example.eatopia.domain.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    @Override
    public Page<ReviewSearchResponse> searchReviews(Long productId, ReviewSearchCondition condition, Pageable pageable) {

        return reviewRepository.searchReviewsByProduct(productId, condition, pageable);
    }

    @Override
    public Page<ReviewSellerResponse> getReviewsBySeller(Long productId, Long sellerId, ReviewSearchCondition condition, Pageable pageable) {

        if (condition.status() == ReviewStatus.HIDDEN) {
            throw new GlobalException(ReviewErrorCode.REVIEW_HIDDEN);
        }

        return reviewRepository.getReviewsBySeller(productId, sellerId, condition, pageable);
    }
}
