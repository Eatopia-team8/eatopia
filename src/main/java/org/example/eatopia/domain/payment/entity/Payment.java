package org.example.eatopia.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.order.entity.Order;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Payment(Order order, PaymentMethod method) {
        this.order = order;
        this.price = order.getFinalPrice();
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public static Payment create(Order order, PaymentMethod method) {
        return Payment.builder()
                .order(order)
                .method(method)
                .build();
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updateMethod(PaymentMethod method) {
        this.method = method;
    }
}
