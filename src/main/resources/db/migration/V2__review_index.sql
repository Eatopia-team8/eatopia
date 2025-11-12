-- 평점 조회 최적화
CREATE INDEX idx_review_product_rating
    ON review (product_id, status, deleted_at, rating DESC);

-- 최신순 조회 최적화
CREATE INDEX idx_review_product_created_at
    ON review (product_id, status, deleted_at, created_at DESC);

-- 판매자/ 관리자 리뷰 조회
CREATE INDEX idx_review_status_deleted_created
    ON review (status, deleted_at, created_at DESC);

CREATE INDEX idx_review_rating_status_deleted_created
    ON review (rating, status, deleted_at, created_at DESC);