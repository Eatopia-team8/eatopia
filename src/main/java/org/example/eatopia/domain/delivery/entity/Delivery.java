package org.example.eatopia.domain.delivery.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.delivery.enums.DeliveryStatus;
import org.example.eatopia.domain.order.entity.Order;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "deliveries")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column
    private LocalDateTime deliveredAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Delivery(Order order) {
        this.order = order;
        this.status = DeliveryStatus.PREPARING;
    }

    public static Delivery from(Order order) {
        return Delivery.builder()
                .order(order)
                .build();
    }

    public void updateStatus(DeliveryStatus status) {
        if (status == DeliveryStatus.DELIVERED && this.deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }

        this.status = status;
    }
}