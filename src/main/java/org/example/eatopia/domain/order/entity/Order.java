package org.example.eatopia.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders") //order = sql 예약어
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalProductPrice;

    //나중에 계산하기 쉽게 초기에 설정
    @Column(nullable = false)
    private BigDecimal discountProductPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalDeliveryPrice;

    @Column(nullable = false)
    private BigDecimal discountDeliveryPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Builder(access = AccessLevel.PRIVATE)
    private Order(Long userId, Long productId, Long sellerId, String code, BigDecimal totalProductPrice, BigDecimal discountProductPrice, BigDecimal totalDeliveryPrice, BigDecimal discountDeliveryPrice, BigDecimal finalPrice) {
        this.userId = userId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.code = code;
        this.status = OrderStatus.PENDING;
        this.totalProductPrice = totalProductPrice;
        this.discountProductPrice = discountProductPrice;
        this.totalDeliveryPrice = totalDeliveryPrice;
        this.discountDeliveryPrice = discountDeliveryPrice;
        this.finalPrice = finalPrice;
    }

    public static Order create(Long userId, Long productId, Long sellerId, String code, BigDecimal totalProductPrice, BigDecimal discountProductPrice, BigDecimal totalDeliveryPrice, BigDecimal discountDeliveryPrice, BigDecimal finalPrice) {
        return Order.builder()
                .userId(userId)
                .productId(productId)
                .sellerId(sellerId)
                .code(code)
                .totalProductPrice(totalProductPrice)
                .discountProductPrice(discountProductPrice)
                .totalDeliveryPrice(totalDeliveryPrice)
                .discountDeliveryPrice(discountDeliveryPrice)
                .finalPrice(finalPrice)
                .build();
    }
}
