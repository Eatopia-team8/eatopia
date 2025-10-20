package org.example.eatopia.domain.order.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderStatus;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.example.eatopia.domain.order.validator.OrderValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    // private final ProductQueryService productQueryService; //product 구현되면 추가 현재는 임의의 값으로 구현
    private static final BigDecimal TEMPORARY_PRODUCT_PRICE = new BigDecimal("10000");
    private static final BigDecimal DEFAULT_DELIVERY_PRICE = new BigDecimal("3000");
    private static final BigDecimal DEFAULT_DISCOUNT_PRICE = BigDecimal.ZERO;
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;

    @Override
    public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
        orderValidator.orderCreateValidate(request);
        //payment 구현을 위해 임시로 값 설정
        BigDecimal totalProductPrice = TEMPORARY_PRODUCT_PRICE;
        BigDecimal totalDeliveryPrice = DEFAULT_DELIVERY_PRICE;
        BigDecimal discountProductPrice = DEFAULT_DISCOUNT_PRICE;
        BigDecimal discountDeliveryPrice = DEFAULT_DISCOUNT_PRICE;

        // 최종 결제 금액 계산
        BigDecimal finalPrice = totalProductPrice
                .subtract(discountProductPrice)
                .add(totalDeliveryPrice)
                .subtract(discountDeliveryPrice);

        //랜덤 주문코드 생성
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = Order.create(
                userId,
                request.productId(),
                request.sellerId(),
                code,
                totalProductPrice,
                discountProductPrice,
                totalDeliveryPrice,
                discountDeliveryPrice,
                finalPrice
        );

        Order savedOrder = orderRepository.save(order);

        return OrderDetailResponse.from(savedOrder);
    }

    @Override
    public OrderDetailResponse successOrder(Long userId, Long orderId) {
        return updateOrderStatus(userId, orderId, OrderStatus.SUCCESS, orderValidator::orderSuccessValidate);
    }

    @Override
    public OrderDetailResponse cancelOrder(Long userId, Long orderId) {
        return updateOrderStatus(userId, orderId, OrderStatus.CANCELED, orderValidator::orderCancelValidate);
    }

    private OrderDetailResponse updateOrderStatus(Long userId, Long orderId, OrderStatus newStatus, Consumer<Order> validator) {
        Order order = orderValidator.findByIdAndUserIdOrThrow(orderId, userId);
        validator.accept(order);
        order.updateStatus(newStatus);
        return OrderDetailResponse.from(order);
    }
}