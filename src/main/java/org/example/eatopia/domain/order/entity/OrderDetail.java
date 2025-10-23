package org.example.eatopia.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.product.entity.Product;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orderDetail")
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderDetailId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private Long quantity;

    //주문 당시의 가격 저장
    @Column(nullable = false)
    private BigDecimal price;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderDetail(Order order, Product product, Long quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.sellerId = product.getSeller().getId();
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderDetail create(Order order, Product product, Long quantity, BigDecimal price) {
        return OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
