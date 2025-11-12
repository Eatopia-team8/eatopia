package org.example.eatopia.domain.settlement.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.settlement.dto.response.SettlementResponse;
import org.example.eatopia.domain.settlement.entity.Settlement;
import org.example.eatopia.domain.settlement.exception.SettlementErrorCode;
import org.example.eatopia.domain.settlement.exception.SettlementException;
import org.example.eatopia.domain.settlement.repository.SettlementRepository;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SettlementQueryServiceImpl implements SettlementQueryService {

    private final SettlementRepository settlementRepository;

    @Override
    public Page<SettlementResponse> getSettlements(UserPrincipal authUser, Pageable pageable) {
        Page<Settlement> settlementPage;

        if (authUser.getUserRole() == UserRole.ADMIN) {
            settlementPage = settlementRepository.findAllWithSeller(pageable);
        } else {
            settlementPage = settlementRepository.findBySellerIdWithSeller(authUser.getId(), pageable);
        }

        return settlementPage.map(SettlementResponse::from);
    }

    @Override
    public SettlementResponse getSettlement(Long settlementId, UserPrincipal authUser) {
        Settlement settlement;

        if (authUser.getUserRole() == UserRole.ADMIN) {
            settlement = settlementRepository.findByIdWithSeller(settlementId)
                    .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));
        } else {
            settlement = settlementRepository.findByIdAndSellerIdWithSeller(settlementId, authUser.getId())
                    .orElseThrow(() -> new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND));
        }

        return SettlementResponse.from(settlement);
    }
}