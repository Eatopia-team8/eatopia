package org.example.eatopia.domain.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"review_id", "user_id"})
        }
)
public class ReviewReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String reason;

    @Builder(access = AccessLevel.PRIVATE)
    private ReviewReport(Review review, User user, String reason) {
        this.review = review;
        this.user = user;
        this.reason = reason;
    }

    public static ReviewReport create(Review review, User user, String reason) {
        return ReviewReport.builder()
                .review(review)
                .user(user)
                .reason(reason)
                .build();
    }
}
