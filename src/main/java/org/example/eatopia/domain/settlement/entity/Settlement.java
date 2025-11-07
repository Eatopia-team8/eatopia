package org.example.eatopia.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.eatopia.common.core.entity.BaseEntity;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.refund.entity.Refund;
import org.example.eatopia.domain.settlement.enums.BankCode;
import org.example.eatopia.domain.settlement.enums.SettlementStatus;
import org.example.eatopia.domain.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "settlements")
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    @Column(nullable = false)
    private BigDecimal totalSaleAmount; // 총 판매액

    @Column(nullable = false)
    private BigDecimal totalCommissionAmount; // 총 발생 수수료

    @Column(nullable = false)
    private BigDecimal totalRefundAmount; // 총 환불액

    @Column(nullable = false)
    private BigDecimal totalCommissionRefundAmount; // 총 환불된 수수료

    @Column(nullable = false)
    private BigDecimal finalSettlementAmount; // 최종 정산액

    @OneToMany(mappedBy = "settlement")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "settlement")
    private List<Refund> refunds = new ArrayList<>();

    private LocalDateTime completedAt;
    private String portonePayoutUid;
    private String failReason;

    @Enumerated(EnumType.STRING)
    private BankCode bankCode;

    private String bankAccount;
    private String bankHolderName;

    @Builder(access = AccessLevel.PRIVATE)
    private Settlement(User seller, BigDecimal totalSaleAmount, BigDecimal totalCommissionAmount, BigDecimal totalRefundAmount, BigDecimal totalCommissionRefundAmount, BigDecimal finalSettlementAmount) {
        this.seller = seller;
        this.status = SettlementStatus.PENDING;
        this.totalSaleAmount = totalSaleAmount;
        this.totalCommissionAmount = totalCommissionAmount;
        this.totalRefundAmount = totalRefundAmount;
        this.totalCommissionRefundAmount = totalCommissionRefundAmount;
        this.finalSettlementAmount = finalSettlementAmount;
    }

    public static Settlement create(User seller, BigDecimal totalSaleAmount, BigDecimal totalCommissionAmount, BigDecimal totalRefundAmount, BigDecimal totalCommissionRefundAmount) {

        BigDecimal netSales = totalSaleAmount.subtract(totalRefundAmount);
        BigDecimal netCommission = totalCommissionAmount.subtract(totalCommissionRefundAmount);
        BigDecimal finalAmount = netSales.subtract(netCommission);

        return Settlement.builder()
                .seller(seller)
                .totalSaleAmount(totalSaleAmount)
                .totalCommissionAmount(totalCommissionAmount)
                .totalRefundAmount(totalRefundAmount)
                .totalCommissionRefundAmount(totalCommissionRefundAmount)
                .finalSettlementAmount(finalAmount)
                .build();
    }

    /**
     * 정산 완료 시 계좌 정보 저장
     */
    public void complete(String portonePayoutUid, BankCode bankCode, String bankAccount, String bankHolderName) {
        this.status = SettlementStatus.COMPLETED;
        this.portonePayoutUid = portonePayoutUid;
        this.completedAt = LocalDateTime.now();
        this.failReason = null;

        this.bankCode = bankCode;
        this.bankAccount = bankAccount;
        this.bankHolderName = bankHolderName;
    }
}