package org.example.eatopia.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.order.enums.OrderStatus;
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
}
