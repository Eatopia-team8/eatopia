package org.example.eatopia.domain.review.service.command;

import org.example.eatopia.domain.review.dto.request.ReviewRequest;
import org.example.eatopia.domain.review.dto.response.ReviewResponse;

public interface ReviewCommandService {

    ReviewResponse createReview(Long orderDetailId, Long userId, ReviewRequest request);
}
