package org.example.eatopia.domain.review.service.command;

import org.example.eatopia.domain.review.dto.request.ReviewReportRequest;
import org.example.eatopia.domain.review.dto.request.ReviewRequest;
import org.example.eatopia.domain.review.dto.response.ReviewReportResponse;
import org.example.eatopia.domain.review.dto.response.ReviewResponse;

public interface ReviewCommandService {

    ReviewResponse createReview(Long orderDetailId, Long userId, ReviewRequest request);

    ReviewResponse updateReview(Long reviewId, Long userId, ReviewRequest request);

    void deleteReview(Long reviewId, Long userId);

    ReviewReportResponse reportReview(Long reviewId, Long userId, ReviewReportRequest request);

    void hideReview(Long reviewId, Long adminId);
}
