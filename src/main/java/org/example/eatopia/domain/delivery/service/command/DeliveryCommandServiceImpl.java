package org.example.eatopia.domain.delivery.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.delivery.dto.request.DeliveryUpdateRequest;
import org.example.eatopia.domain.delivery.dto.response.DeliveryResponse;
import org.example.eatopia.domain.delivery.entity.Delivery;
import org.example.eatopia.domain.delivery.repository.DeliveryRepository;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryCommandServiceImpl implements DeliveryCommandService {

    private final DeliveryRepository deliveryRepository;

    @Override
    public DeliveryResponse updateDeliveryStatus(Long orderId, DeliveryUpdateRequest request, UserPrincipal principal) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND, "배달 정보를 찾을 수 없습니다."));

        delivery.updateStatus(request.status());

        return DeliveryResponse.from(delivery);
    }
}