package org.example.eatopia.domain.delivery.service.command;

import org.example.eatopia.domain.delivery.dto.request.DeliveryUpdateRequest;
import org.example.eatopia.domain.delivery.dto.response.DeliveryResponse;
import org.example.eatopia.domain.user.dto.UserPrincipal;

public interface DeliveryCommandService {

    DeliveryResponse updateDeliveryStatus(Long orderId, DeliveryUpdateRequest request, UserPrincipal principal);
}