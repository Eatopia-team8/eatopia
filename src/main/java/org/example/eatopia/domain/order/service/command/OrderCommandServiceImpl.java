package org.example.eatopia.domain.order.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.example.eatopia.domain.order.validator.OrderValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    // private final ProductQueryService productQueryService; //product 구현되면 추가 현재는 임의의 값으로 구현

    @Override
    public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
        orderValidator.orderCreateValidate(request);
        //payment 구현을 위해 임시로 값 설정
        BigDecimal totalProductPrice = new BigDecimal("10000");
        BigDecimal totalDeliveryPrice = new BigDecimal("3000");
        BigDecimal discountProductPrice = BigDecimal.ZERO;
        BigDecimal discountDeliveryPrice = BigDecimal.ZERO;

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
}