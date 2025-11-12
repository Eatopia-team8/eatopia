package org.example.eatopia.domain.order.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.settlement.entity.Settlement;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_details")
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
    private Integer quantity;

    //주문 당시의 가격 저장
    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal commission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlementId")
    private Settlement settlement;

    @Builder(access = AccessLevel.PRIVATE)
    private OrderDetail(Order order, Product product, Integer quantity, BigDecimal price, BigDecimal commission) {
        this.order = order;
        this.product = product;
        this.sellerId = product.getSeller().getId();
        this.quantity = quantity;
        this.price = price;
        this.commission = commission;
    }

    public static OrderDetail create(Order order, Product product, Integer quantity, BigDecimal price, BigDecimal commission) {
        return OrderDetail.builder()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .commission(commission)
                .build();
    }

    void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }

    /**
     * [부하 테스트용] BaseEntity의 protected createdAt을 강제로 설정합니다.
     */
    /*
    public void forceSetCreatedAt(java.time.LocalDateTime dateTime) {
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
