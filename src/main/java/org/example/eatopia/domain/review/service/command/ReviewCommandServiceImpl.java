package org.example.eatopia.domain.review.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.review.dto.request.ReviewReportRequest;
import org.example.eatopia.domain.review.dto.request.ReviewRequest;
import org.example.eatopia.domain.review.dto.response.ReviewReportResponse;
import org.example.eatopia.domain.review.dto.response.ReviewResponse;
import org.example.eatopia.domain.review.entity.Review;
import org.example.eatopia.domain.review.entity.ReviewReport;
import org.example.eatopia.domain.review.exception.ReviewErrorCode;
import org.example.eatopia.domain.review.repository.ReviewReportRepository;
import org.example.eatopia.domain.review.repository.ReviewRepository;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    private final OrderQueryService orderQueryService;
    private final UserQueryService userQueryService;
    private final ProductQueryService productQueryService;

    @Override
    public ReviewResponse createReview(Long orderDetailId, Long userId, ReviewRequest request) {

        // 중복 리뷰 작성 방지
        if (reviewRepository.existsByOrderDetailId(orderDetailId)) {
            throw new GlobalException(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 주문 내역 있는지 확인
        OrderDetail orderDetail = orderQueryService.getOrderDetailByUserId(orderDetailId, userId);

        Review review = Review.create(
                orderDetail.getOrder().getUser(),
                orderDetail.getProduct(),
                orderDetail,
                request.content(),
                request.rating()
        );
        reviewRepository.save(review);

        return ReviewResponse.fromForCreate(review);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, Long userId, ReviewRequest request) {

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new GlobalException(ReviewErrorCode.REVIEW_NOT_FOUND));

        // 활성화 상태, 삭제되지 않은 리뷰만 수정 가능
        if (!review.isUpdatable()) {
            throw new GlobalException(ReviewErrorCode.REVIEW_CANNOT_UPDATE);
        }

        review.update(request.content(), request.rating());

        return ReviewResponse.fromForUpdate(review);
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new GlobalException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (review.getDeletedAt() != null) {
            throw new GlobalException(ReviewErrorCode.REVIEW_ALREADY_DELETED);
        }

        review.softDelete();
    }

    @Override
    public ReviewReportResponse reportReview(Long reviewId, Long userId, ReviewReportRequest request) {

        // 중복 신고 불가
        if (reviewReportRepository.existsByIdAndUserId(reviewId, userId)) {
            throw new GlobalException(ReviewErrorCode.REVIEW_ALREADY_REPORTED);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new GlobalException(ReviewErrorCode.REVIEW_NOT_FOUND));
        User user = userQueryService.getUserEntityById(userId);

        ReviewReport report = ReviewReport.create(review, user, request.reason());
        reviewReportRepository.save(report);

        // 리뷰 상태 변경
        review.report();

        return null;
    }
}
