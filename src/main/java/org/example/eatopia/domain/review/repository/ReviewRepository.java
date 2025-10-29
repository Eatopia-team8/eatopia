package org.example.eatopia.domain.review.repository;

import org.example.eatopia.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByOrderDetailId(Long orderDetailId);

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);
}
