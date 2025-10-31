package org.example.eatopia.domain.order.service.command;

import lombok.RequiredArgsConstructor;
import org.example.eatopia.domain.address.dto.AddressResponse;
import org.example.eatopia.domain.address.service.query.AddressQueryService;
import org.example.eatopia.domain.cart.entity.CartItem;
import org.example.eatopia.domain.cart.service.command.CartCommandService;
import org.example.eatopia.domain.cart.service.query.CartQueryService;
import org.example.eatopia.domain.coupon.service.command.CouponCommandService;
import org.example.eatopia.domain.coupon.service.query.CouponQueryService;
import org.example.eatopia.domain.order.dto.event.OrderCancelledEvent;
import org.example.eatopia.domain.order.dto.request.OrderCreateRequest;
import org.example.eatopia.domain.order.dto.response.OrderDetailResponse;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.example.eatopia.domain.order.exception.OrderErrorCode;
import org.example.eatopia.domain.order.exception.OrderException;
import org.example.eatopia.domain.order.repository.OrderDetailRepository;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.example.eatopia.domain.order.validator.OrderValidator;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.service.command.ProductCommandService;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.service.query.UserQueryService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.example.eatopia.common.core.consts.Const.DEFAULT_DELIVERY_PRICE;
import static org.example.eatopia.common.core.consts.Const.DELIVERY_FREE_THRESHOLD;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderCommandServiceImpl implements OrderCommandService {

    private static final BigDecimal DEFAULT_DISCOUNT_PRICE = BigDecimal.ZERO;

    private final UserQueryService userQueryService;
    private final CartQueryService cartQueryService;
    private final CouponQueryService couponQueryService;
    private final AddressQueryService addressQueryService;

    private final ProductCommandService productCommandService;
    private final CartCommandService cartCommandService;
    private final CouponCommandService couponCommandService;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final OrderValidator orderValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public OrderDetailResponse createOrder(Long userId, OrderCreateRequest request) {
        User user = userQueryService.getUserEntityById(userId);
        AddressResponse address = addressQueryService.getAddressById(userId, request.addressId());
        List<CartItem> cartItems = cartQueryService.getSelectedCartItems(userId);

        orderValidator.validateCartItems(cartItems);

        //금액 계산
        BigDecimal totalProductPrice = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            orderValidator.validateStock(product, quantity);

            totalProductPrice = totalProductPrice.add(
                    product.getPrice().multiply(BigDecimal.valueOf(quantity))
            );
        }

        // 쿠폰 선택
        /*
        CouponIssue couponIssue = couponQueryService.getUsableIssuedCoupons(userId);
         */
        Long couponIssueId = null;
        BigDecimal discountProductPrice = BigDecimal.ZERO;
        BigDecimal discountDeliveryPrice = BigDecimal.ZERO;
        if (totalProductPrice.compareTo(DELIVERY_FREE_THRESHOLD) >= 0) {
            discountDeliveryPrice = DEFAULT_DELIVERY_PRICE;
        }

        /*
        if (couponIssue != null) {
            discountProductPrice = couponCommandService.calculateDiscountValue(totalProductPrice, couponIssue);
            //쿠폰 등록 , 환불 로직에 필요
            couponIssueId = couponIssue.getId();
        }
         */
        //최종 금액 계산
        BigDecimal totalDeliveryPrice = DEFAULT_DELIVERY_PRICE;
        BigDecimal finalPrice = totalProductPrice
                .subtract(discountProductPrice)
                .add(totalDeliveryPrice)
                .subtract(discountDeliveryPrice);

        orderValidator.validateFinalPrice(finalPrice);

        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = Order.create(
                user,
                code,
                totalProductPrice,
                discountProductPrice,
                totalDeliveryPrice,
                discountDeliveryPrice,
                finalPrice,
                couponIssueId,
                address.address()
        );
        Order savedOrder = orderRepository.save(order);

        // 가격 저장
        List<OrderDetail> orderDetail = cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    OrderDetail detail = OrderDetail.create(
                            savedOrder,
                            product,
                            cartItem.getQuantity(),
                            product.getPrice()
                    );
                    savedOrder.addOrderDetail(detail);
                    return detail;
                })
                .collect(Collectors.toList());

        orderDetailRepository.saveAll(orderDetail);

        List<Long> productDelete = cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getId())
                .toList();

        cartCommandService.deleteOrderedItems(userId, productDelete);

        return OrderDetailResponse.from(savedOrder);
    }

    @Override
    public OrderDetailResponse successOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        orderValidator.orderSuccessValidate(order);

        //주문 성공하면 재고 차감
        List<OrderDetail> orderDetail = order.getOrderDetails();

        for (OrderDetail detail : orderDetail) {
            productCommandService.decreaseStock(detail.getProduct().getId(), detail.getQuantity());
        }

        //주문 성공하면 쿠폰 사용
        /*
        Long issueId = order.getCouponIssueId();
        if (issueId != null) {
            couponCommandService.useCoupon(issueId);
        }
        */
        order.updateStatus(OrderStatus.SUCCESS);
        order.startDelivery();

        return OrderDetailResponse.from(order);
    }

    /**
     * 주문 취소시 payment도 취소
     */
    @Override
    public OrderDetailResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByUserIdAndId(userId, orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        orderValidator.orderCancelValidate(order);

        // SUCCESS 상태일 때만 결제 취소 및 재고 롤백
        if (order.getStatus() == OrderStatus.SUCCESS) {
            eventPublisher.publishEvent(new OrderCancelledEvent(order));

            List<OrderDetail> orderDetails = order.getOrderDetails();
            for (OrderDetail detail : orderDetails) {
                productCommandService.increaseStock(detail.getProduct().getId(), detail.getQuantity());
            }

            //주문 취소시 쿠폰 롤백
            /*
            Long issueId = order.getCouponIssueId();
            if (issueId != null) {
                couponCommandService.rollbackCoupon(issueId);
            }
            */
        }
        // PENDING 상태일 땐 CANCELED로 변경
        order.updateStatus(OrderStatus.CANCELED);

        return OrderDetailResponse.from(order);
    }
}