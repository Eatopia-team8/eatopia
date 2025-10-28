package org.example.eatopia.domain.review.repository;

import org.example.eatopia.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    boolean existsByOrderDetailId(Long orderDetailId);
}
