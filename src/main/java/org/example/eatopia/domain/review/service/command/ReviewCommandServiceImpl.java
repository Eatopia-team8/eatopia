package org.example.eatopia.domain.review.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.example.eatopia.domain.review.dto.request.ReviewRequest;
import org.example.eatopia.domain.review.dto.response.ReviewResponse;
import org.example.eatopia.domain.review.entity.Review;
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
        OrderDetail orderDetail = orderQueryService.getOrderDetailbByUserId(orderDetailId, userId);

        User user = userQueryService.getUserEntityById(userId);
        Product product = productQueryService.getProductOrElseThrow(orderDetail.getProduct().getId());
        Review review = Review.create(
                user, product,
                orderDetail,
                request.content(),
                request.rating()
        );
        reviewRepository.save(review);

        return ReviewResponse.fromForCreate(review);
    }
}
