package org.example.eatopia.domain.refund.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.refund.dto.response.RefundResponse;
import org.example.eatopia.domain.refund.repository.RefundRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
class RefundQueryServiceImpl implements RefundQueryService {

    private final RefundRepository refundRepository;

    @Override
    public Page<RefundResponse> getRefunds(Long userId, Pageable pageable) {

        return refundRepository.findByUserId(userId, pageable)
                .map(RefundResponse::from);
    }
}
