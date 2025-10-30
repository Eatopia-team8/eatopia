package org.example.eatopia.domain.review.service.query;

import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewAdminResponse;
import org.example.eatopia.domain.review.dto.response.ReviewReportResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSellerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewQueryService {

    /**
     * 특정 상품에 대한 리뷰를 조회합니다.
     * <p>
     * 검색 조건(키워드, 평점 등)에 따라 필터링하고,
     * 페이징 및 정렬 조건(Pageable)을 적용합니다.
     *
     * @param productId 조회할 상품 ID
     * @param condition 검색 조건 (키워드, 평점 등)
     * @param pageable  페이징 및 정렬 정보
     * @return 상품 리뷰 목록 페이지
     */
    Page<ReviewSearchResponse> searchReviews(Long productId, ReviewSearchCondition condition, Pageable pageable);

    /**
     * 판매자 기준으로 리뷰를 조회합니다.
     * <p>
     * 특정 상품의 리뷰만 조회할 수도 있고(productId 지정 시),
     * 판매자의 모든 상품 리뷰를 조회할 수도 있습니다.
     * 검색 조건과 페이징, 정렬이 모두 지원됩니다.
     *
     * @param productId 조회할 상품 ID (선택적)
     * @param sellerId  판매자 ID
     * @param condition 검색 조건 (키워드, 평점, 상태, 삭제 여부 등)
     * @param pageable  페이징 및 정렬 정보
     * @return 판매자용 리뷰 목록 페이지
     */
    Page<ReviewSellerResponse> getReviewsForSeller(Long productId, Long sellerId, ReviewSearchCondition condition, Pageable pageable);

    /**
     * 관리자 권한으로 리뷰를 조회합니다.
     * <p>
     * 특정 상품이나 사용자 기준으로 필터링할 수 있으며(선택적),
     * 전체 리뷰를 조회하는 것도 가능합니다.
     * 검색 조건, 페이징, 정렬이 모두 지원됩니다.
     *
     * @param productId 조회할 상품 ID (선택적)
     * @param userId    조회할 사용자 ID (선택적)
     * @param condition 검색 조건 (키워드, 평점, 상태, 삭제 여부 등)
     * @param pageable  페이징 및 정렬 정보
     * @return 관리자용 리뷰 목록 페이지
     */
    Page<ReviewAdminResponse> getReviewsForAdmin(Long productId, Long userId, ReviewSearchCondition condition, Pageable pageable);

    Page<ReviewReportResponse> getReviewReports(Long reviewId, Pageable pageable);
}
