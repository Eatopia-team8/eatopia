package org.example.eatopia.domain.review.repository;

import org.example.eatopia.domain.review.entity.ReviewReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
}
