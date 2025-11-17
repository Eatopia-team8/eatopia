package org.example.eatopia.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.delivery.entity.Delivery;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders") //order = sql 예약어
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalProductPrice;

    @Column(nullable = false)
    private BigDecimal discountProductPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal totalDeliveryPrice;

    @Column(nullable = false)
    private BigDecimal discountDeliveryPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal finalPrice;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "couponIssueId")
    private Long couponIssueId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Delivery delivery;

    @Builder(access = AccessLevel.PRIVATE)
    private Order(User user,
                  String code,
                  BigDecimal totalProductPrice,
                  BigDecimal discountProductPrice,
                  BigDecimal totalDeliveryPrice,
                  BigDecimal discountDeliveryPrice,
                  BigDecimal finalPrice,
                  Long couponIssueId,
                  String address) {
        this.user = user;
        this.code = code;
        this.status = OrderStatus.PENDING;
        this.totalProductPrice = totalProductPrice;
        this.discountProductPrice = discountProductPrice;
        this.totalDeliveryPrice = totalDeliveryPrice;
        this.discountDeliveryPrice = discountDeliveryPrice;
        this.finalPrice = finalPrice;
        this.couponIssueId = couponIssueId;
        this.address = address;
    }

    public static Order create(User user,
                               String code,
                               BigDecimal totalProductPrice,
                               BigDecimal discountProductPrice,
                               BigDecimal totalDeliveryPrice,
                               BigDecimal discountDeliveryPrice,
                               BigDecimal finalPrice,
                               Long couponIssueId,
                               String address) {
        return Order.builder()
                .user(user)
                .code(code)
                .totalProductPrice(totalProductPrice)
                .discountProductPrice(discountProductPrice)
                .totalDeliveryPrice(totalDeliveryPrice)
                .discountDeliveryPrice(discountDeliveryPrice)
                .finalPrice(finalPrice)
                .couponIssueId(couponIssueId)
                .address(address)
                .build();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
    }

    /**
     * 주문 성공시 배달 상품 준비
     */
    public void startDelivery() {
        if (this.status != OrderStatus.SUCCESS || this.delivery != null) {
            return;
        }

        this.delivery = Delivery.from(this);
    }

    public void assignDetailToSettlement(Long orderDetailId, Settlement settlement) {

        OrderDetail detailToSettle = this.orderDetails.stream()
                .filter(od -> od.getId().equals(orderDetailId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Order " + this.id + "에 OrderDetail " + orderDetailId + "가 존재하지 않습니다."));
        detailToSettle.setSettlement(settlement);
    }

    /**
     * [부하 테스트용] BaseEntity의 protected createdAt을 강제로 설정합니다.
     */
    /*
    public void forceSetCreatedAt(java.time.LocalDateTime dateTime) {
        // BaseEntity 또는 공통 상위 엔티티의 createdAt 필드를 직접 설정합니다.
        // 필드명이나 상속 구조에 따라 'super.createdAt = dateTime;' 등으로 수정해야 할 수 있습니다.
        try {
            java.lang.reflect.Field field = getClass().getSuperclass().getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(this, dateTime);
        } catch (Exception e) {
            throw new RuntimeException("BaseEntity의 createdAt 필드 설정 실패", e);
        }
    }
     */
}
