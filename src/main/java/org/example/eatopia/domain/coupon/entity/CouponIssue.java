package org.example.eatopia.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.domain.user.entity.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "coupon_issue",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_coupon_issue_user_coupon",
                        columnNames = {"user_id", "coupon_id"}
                )
        }
)
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @CreatedDate
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Builder
    private CouponIssue(User user, Coupon coupon) {
        this.user = user;
        this.coupon = coupon;
    }

    public static CouponIssue of(User user, Coupon coupon) {
        return CouponIssue.builder()
                .user(user)
                .coupon(coupon)
                .build();
    }

    public void useIssuedCoupon() {
        this.usedAt = LocalDateTime.now();
    }
}