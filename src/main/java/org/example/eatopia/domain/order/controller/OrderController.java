package org.example.eatopia.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.dto.response.OrderResponse;
import org.example.eatopia.domain.order.service.command.OrderCommandService;
import org.example.eatopia.domain.order.service.query.OrderQueryService;
import org.example.eatopia.domain.user.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order API", description = "주문 관련 API")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    @Operation(summary = "주문 생성", description = "주문을 생성합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "생성 성공"),
                    @ApiResponse(responseCode = "400", description = "생성 실패")
            })
    @PreAuthorize("hasRole('ROLE_BUYER')")
    @PostMapping
    public ResponseEntity<OrderDetailResponse> createOrder(
            @AuthenticationPrincipal UserPrincipal authUser,
            @Valid @RequestBody OrderCreateRequest request
    ) {
        Long userId = authUser.getId();

        OrderDetailResponse createdOrder = orderCommandService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "주문 상세 조회", description = "사용자의 주문을 상세 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "조회 실패")
            })
    @PreAuthorize("hasRole('ROLE_BUYER')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderDetailResponse orderDetail = orderQueryService.getOrder(userId, orderId);
        return ResponseEntity.ok(orderDetail);
    }

    @Operation(summary = "주문 목록 조회", description = "사용자의 주문 내역 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "조회 실패")
            })
    @PreAuthorize("hasRole('ROLE_BUYER')")
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = authUser.getId();
        Page<OrderResponse> ordersPage = orderQueryService.getOrders(userId, pageable);
        return ResponseEntity.ok(ordersPage);
    }

    @Operation(summary = "주문 취소", description = "주문을 취소합니다.",
            security = {@SecurityRequirement(name = "bearerAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "취소 성공"),
                    @ApiResponse(responseCode = "400", description = "취소 실패")
            })
    @PreAuthorize("hasRole('ROLE_BUYER')")
    @PatchMapping("{orderId}/cancel")
    public ResponseEntity<OrderDetailResponse> cancelOrder(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderDetailResponse order = orderCommandService.cancelOrder(userId, orderId);

        return ResponseEntity.ok(order);
    }
}
