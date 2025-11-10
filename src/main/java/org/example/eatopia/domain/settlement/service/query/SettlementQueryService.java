package org.example.eatopia.domain.settlement.service.query;

import org.example.eatopia.domain.settlement.dto.response.SettlementResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SettlementQueryService {

    Page<SettlementResponse> getSettlements(UserPrincipal authUser, Pageable pageable);
    
    SettlementResponse getSettlement(Long settlementId, UserPrincipal authUser);
}