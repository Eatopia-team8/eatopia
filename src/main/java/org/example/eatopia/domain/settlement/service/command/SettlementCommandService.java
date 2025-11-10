package org.example.eatopia.domain.settlement.service.command;

import org.example.eatopia.domain.settlement.dto.request.SettlementCreateRequest;
import org.example.eatopia.domain.settlement.dto.response.SettlementResponse;

public interface SettlementCommandService {

    SettlementResponse requestSettlement(Long sellerId, SettlementCreateRequest request);

    void processPayout(Long settlementId, SettlementCreateRequest request);
}