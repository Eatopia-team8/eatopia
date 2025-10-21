package org.example.eatopia.domain.order.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.common.core.exception.GlobalException;
import org.example.eatopia.domain.order.dto.event.OrderCancelledEvent;
import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderStatus;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.example.eatopia.domain.order.validator.OrderValidator;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.service.command.ProductCommandService;
import org.example.eatopia.domain.product.service.query.ProductQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    private static final BigDecimal DEFAULT_DELIVERY_PRICE = new BigDecimal("3000");
    private static final BigDecimal DEFAULT_DISCOUNT_PRICE = BigDecimal.ZERO;
    private final ProductQueryService productQueryService;
    private final ProductCommandService productCommandService;
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
        orderValidator.orderCreateValidate(request);

        Product product = productQueryService.getProductOrElseThrow(request.productId());
        orderValidator.validateStock(product, request.quantity());

        BigDecimal totalProductPrice = product.getPrice()
                .multiply(new BigDecimal(request.quantity()));
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
                request.quantity(),
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
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));

        orderValidator.orderSuccessValidate(order);

        //주문 성공하면 재고 차감
        productCommandService.decreaseStock(order.getProductId(), order.getQuantity());

        order.updateStatus(OrderStatus.SUCCESS);
        return OrderDetailResponse.from(order);
    }

    /**
     * 주문 취소시 payment도 취소
     */
    @Override
    public OrderDetailResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new GlobalException(OrderErrorCode.ORDER_NOT_FOUND));

        orderValidator.orderCancelValidate(order);
        order.updateStatus(OrderStatus.CANCELED);
        eventPublisher.publishEvent(new OrderCancelledEvent(order));
        productCommandService.increaseStock(order.getProductId(), order.getQuantity());
        return OrderDetailResponse.from(order);
    }
}