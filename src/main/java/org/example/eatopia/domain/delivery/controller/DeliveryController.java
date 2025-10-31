package org.example.eatopia.domain.delivery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.dto.Response;
import org.example.eatopia.domain.delivery.dto.request.DeliveryUpdateRequest;
import org.example.eatopia.domain.delivery.dto.response.DeliveryResponse;
import org.example.eatopia.domain.delivery.service.command.DeliveryCommandService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Delivery API", description = "배달 관리 API")
@RestController
@RequestMapping("/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryCommandService deliveryCommandService;

    @Operation(summary = "배달 상태 변경",
            description = "배달의 상태를 변경합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Response<DeliveryResponse>> updateDeliveryStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody DeliveryUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        DeliveryResponse response = deliveryCommandService.updateDeliveryStatus(orderId, request, principal);
        return ResponseEntity.ok(Response.success(response));
    }
}