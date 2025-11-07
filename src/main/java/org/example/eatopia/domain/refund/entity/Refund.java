package org.example.eatopia.domain.refund.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.consts.Const;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.payment.entity.Payment;
import org.example.eatopia.domain.refund.enums.RefundReason;
import org.example.eatopia.domain.refund.enums.RefundStatus;
import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    private BigDecimal amount; // 환불 금액

    @Column(nullable = false)
    private BigDecimal price; // 상품 가격

    @Column(nullable = false)
    private Integer quantity; // 환불 수량

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @Column(nullable = false)
    private BigDecimal commissionAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlementId")
    private Settlement settlement;

    @Builder(access = AccessLevel.PRIVATE)
    private Refund(User user, Payment payment, OrderDetail orderDetail, BigDecimal price, Integer quantity, RefundReason reason) {
        this.user = user;
        this.payment = payment;
        this.orderDetail = orderDetail;
        this.price = price;
        this.quantity = quantity;
        this.amount = price.multiply(BigDecimal.valueOf(quantity));
        this.commissionAmount = this.amount.multiply(Const.COMMISSION_RATE)
                .setScale(0, RoundingMode.FLOOR);
        this.reason = reason;
        this.status = RefundStatus.PENDING;
    }

    public static Refund of(User user, Payment payment, OrderDetail orderDetail, BigDecimal price, Integer quantity, RefundReason reason) {
        return Refund.builder()
                .user(user)
                .payment(payment)
                .orderDetail(orderDetail)
                .price(price)
                .quantity(quantity)
                .reason(reason)
                .build();
    }

    public void updateStatus(RefundStatus status) {
        this.status = status;
    }

    void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }
}