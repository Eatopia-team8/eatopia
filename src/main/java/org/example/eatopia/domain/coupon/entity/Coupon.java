package org.example.eatopia.domain.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.eatopia.common.core.entity.SoftDeleteEntity;
import org.example.eatopia.domain.coupon.dto.request.CouponCreateRequest;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "user_id", nullable = false)
    //private User user;

    @NotBlank
    @Size(max = 30)
    @Column(name = "code", nullable = false, length = 30, unique = true)
    private String code;

    @NotBlank
    @Size(max = 50)
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank
    @Size(max = 255)
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;


    @Column(name = "is_active", nullable = false, columnDefinition = "boolean default false")
    private Boolean isActive;

    @Column(name = "is_new_user_only", nullable = false, columnDefinition = "boolean default false")
    private Boolean isNewUserOnly;

    @Column(name = "is_first_order_only", nullable = false, columnDefinition = "boolean default false")
    private Boolean isFirstOrderOnly;

    @Min(0)
    @Column(name = "usage_limit")
    private Integer usageLimit;

    // null일 경우 무제한 발급 쿠폰
    // totalQuantity = issuedQuantity + remainingQuantity
    @Min(0)
    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Min(0)
    @Column(name = "issued_quantity", nullable = false, columnDefinition = "int default 0")
    private Integer issuedQuantity;

    @Min(0)
    @Column(name = "remaining_quantity", nullable = false, columnDefinition = "int default 0")
    private Integer remainingQuantity;

    @Column(name = "is_percent", nullable = false, columnDefinition = "boolean default true")
    private Boolean percent = true;

    // percent면 0~100, fixed면 통화단위 금액
    @NotNull
    @Column(name = "discount_value", nullable = false, precision = 19)
    private BigDecimal discountValue;

    // percent일 때만 사용(선택), NULL 허용
    @Column(name = "max_discount_amount", precision = 19)
    private BigDecimal maxDiscountAmount;

    // 최소 결제 금액 조건(없으면 NULL)
    @Column(name = "min_order_amount", precision = 19)
    private BigDecimal minOrderAmount;

    public static Coupon of(CouponCreateRequest request) {

        return Coupon.builder()
                .code(generateRandomCode())
                .name(request.name())
                .description(request.description())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .isActive(request.isActive())
                .isFirstOrderOnly(request.isFirstOrderOnly())
                .isNewUserOnly(request.isNewUserOnly())
                .usageLimit(request.usageLimit())
                .totalQuantity(request.totalQuantity())
                .issuedQuantity(0)
                .remainingQuantity(request.totalQuantity() == null ? 0 : request.totalQuantity())
                .percent(request.isPercent())
                .discountValue(request.discountValue())
                .maxDiscountAmount(request.maxDiscountAmount() != null ? (request.maxDiscountAmount()) : null)
                .minOrderAmount(request.minOrderAmount() != null ? request.minOrderAmount() : null)
                .build();
    }

    private static String generateRandomCode() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(8);
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}