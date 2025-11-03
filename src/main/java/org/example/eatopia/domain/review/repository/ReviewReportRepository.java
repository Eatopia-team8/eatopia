package org.example.eatopia.domain.review.repository;

import org.example.eatopia.domain.review.entity.ReviewReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    @EntityGraph(attributePaths = {"user"})
    Page<ReviewReport> findAllByReviewId(Long reviewId, Pageable pageable);
}
