package org.example.eatopia.domain.delivery.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.delivery.dto.request.DeliveryUpdateRequest;
import org.example.eatopia.domain.delivery.dto.response.DeliveryResponse;
import org.example.eatopia.domain.delivery.entity.Delivery;
import org.example.eatopia.domain.delivery.exception.DeliveryErrorCode;
import org.example.eatopia.domain.delivery.exception.DeliveryException;
import org.example.eatopia.domain.delivery.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryCommandServiceImpl implements DeliveryCommandService {

    private final DeliveryRepository deliveryRepository;

    @Override
    public DeliveryResponse updateDeliveryStatus(Long orderId, DeliveryUpdateRequest request) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        delivery.updateStatus(request.status());

        return DeliveryResponse.from(delivery);
    }
}