package org.example.eatopia.domain.settlement.service.command;

import org.example.eatopia.domain.settlement.dto.request.SettlementCreateRequest;

public interface SettlementTransactionalService {

    void completeSettlementInNewTransaction(Long settlementId, String payoutUid, SettlementCreateRequest request);

    void failSettlementInNewTransaction(Long settlementId, String failReason);
}