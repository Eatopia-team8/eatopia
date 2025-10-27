package org.example.eatopia.domain.order.service.query;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.repository.OrderDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderDetailQueryServiceImpl implements OrderDetailQueryService {
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public OrderDetail getOrderDetailEntityById(Long orderDetailId) {
        return orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}
