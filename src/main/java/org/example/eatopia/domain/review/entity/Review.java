package org.example.eatopia.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.SoftDeleteEntity;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.review.enums.ReviewStatus;
import org.example.eatopia.domain.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", unique = true, nullable = false)
    private OrderDetail orderDetail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "TINYINT CHECK (rating BETWEEN 1 AND 5)")
    private Integer rating;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    private LocalDateTime reportedAt;

    private int reportCount;

    private Long handledById;

    @Builder(access = AccessLevel.PRIVATE)
    private Review(User user,
                   Product product,
                   OrderDetail orderDetail,
                   String content,
                   Integer rating,
                   ReviewStatus status,
                   LocalDateTime reportedAt,
                   int reportCount,
                   Long handledById) {
        this.user = user;
        this.product = product;
        this.orderDetail = orderDetail;
        this.content = content;
        this.rating = rating;
        this.status = status;
        this.reportedAt = reportedAt;
        this.reportCount = reportCount;
        this.handledById = handledById;
    }

    public static Review create(User user, Product product, OrderDetail orderDetail, String content, Integer rating) {
        return Review.builder()
                .user(user)
                .product(product)
                .orderDetail(orderDetail)
                .content(content)
                .rating(rating)
                .status(ReviewStatus.ACTIVE)
                .reportCount(0)
                .build();
    }

}
