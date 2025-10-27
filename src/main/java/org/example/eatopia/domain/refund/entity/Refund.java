package org.example.eatopia.domain.refund.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.refund.enums.RefundReason;
import org.example.eatopia.domain.refund.enums.RefundStatus;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refunds")
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refundId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId", nullable = false)
    private Payment payment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderDetailId", nullable = false, unique = true)
    private OrderDetail orderDetail;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private Refund(User user, Payment payment, OrderDetail orderDetail, BigDecimal amount, RefundReason reason) {
        this.user = user;
        this.payment = payment;
        this.orderDetail = orderDetail;
        this.amount = amount;
        this.reason = reason;
        this.status = RefundStatus.PENDING;
    }

    public static Refund of(User user, Payment payment, OrderDetail orderDetail, BigDecimal amount, RefundReason reason) {
        return Refund.builder()
                .user(user)
                .payment(payment)
                .orderDetail(orderDetail)
                .amount(amount)
                .reason(reason)
                .build();
    }

    public void updateStatus(RefundStatus status) {
        this.status = status;
    }
}