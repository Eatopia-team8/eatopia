package org.example.eatopia.domain.review.repository;

import org.example.eatopia.domain.review.dto.request.ReviewSearchCondition;
import org.example.eatopia.domain.review.dto.response.ReviewAdminResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSearchResponse;
import org.example.eatopia.domain.review.dto.response.ReviewSellerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {

    /**
     * 특정 상품에 대한 리뷰를 조회합니다.
     * <p>
     * 검색 조건(키워드, 평점)에 따라 필터링하며,
     * 페이징 및 정렬 조건(Pageable)을 적용합니다.
     *
     * @param productId 조회할 상품 ID
     * @param condition 검색 조건 (키워드, 평점 등)
     * @param pageable  페이징 및 정렬 정보
     * @return 상품 리뷰 목록 페이지
     */
    Page<ReviewSearchResponse> searchReviewsByProduct(Long productId, ReviewSearchCondition condition, Pageable pageable);

    /**
     * 판매자 기준으로 상품의 리뷰를 조회합니다.
     * <p>
     * 특정 상품에 한정할 수도 있고(선택적),
     * 전체 상품 리뷰를 조회할 수도 있습니다.
     * 검색 조건과 페이징 및 정렬 처리가 지원됩니다.
     *
     * @param productId 조회할 상품 ID (선택적)
     * @param sellerId  판매자 ID
     * @param condition 검색 조건 (키워드, 평점, 상태, 삭제 여부 등)
     * @param pageable  페이징 및 정렬 정보
     * @return 판매자용 리뷰 목록 페이지
     */
    Page<ReviewSellerResponse> getReviewsForSeller(Long productId, Long sellerId, ReviewSearchCondition condition, Pageable pageable);

    /**
     * 관리자가 리뷰를 조회합니다.
     * <p>
     * 특정 상품이나 특정 사용자를 기준으로 조회할 수 있으며(선택적),
     * 전체 리뷰 검색도 가능합니다.
     * 검색 조건과 페이징 및 정렬 처리가 지원됩니다.
     *
     * @param productId 조회할 상품 ID (선택적)
     * @param userId    조회할 사용자 ID (선택적)
     * @param condition 검색 조건 (키워드, 평점, 상태, 삭제 여부, 신고 리뷰만 등)
     * @param pageable  페이징 및 정렬 정보
     * @return 관리자용 리뷰 목록 페이지
     */
    Page<ReviewAdminResponse> getReviewsForAdmin(Long productId, Long userId, ReviewSearchCondition condition, Pageable pageable);
}
