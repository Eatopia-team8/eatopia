package org.example.eatopia.domain.order.controller;

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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;

    /**
     * SecurityContext에서 사용자 정보 추출
     */
    @PostMapping
    public ResponseEntity<OrderDetailResponse> createOrder(
            @AuthenticationPrincipal UserPrincipal authUser,
            @RequestBody OrderCreateRequest request
    ) {
        Long userId = authUser.getId();

        OrderDetailResponse createdOrder = orderCommandService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PathVariable Long orderId
    ) {
        Long userId = authUser.getId();
        OrderDetailResponse orderDetail = orderQueryService.getOrder(userId, orderId);
        return ResponseEntity.ok(orderDetail);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(
            @AuthenticationPrincipal UserPrincipal authUser,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = authUser.getId();
        Page<OrderResponse> ordersPage = orderQueryService.getOrders(userId, pageable);
        return ResponseEntity.ok(ordersPage);
    }

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
