package org.example.eatopia.domain.review.repository;

import jakarta.persistence.LockModeType;
import org.example.eatopia.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    boolean existsByOrderDetailId(Long orderDetailId);

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId);
}
