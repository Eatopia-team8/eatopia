package org.example.eatopia.domain.statistic.service.query;

import org.example.eatopia.domain.address.entity.Address;
import org.example.eatopia.domain.address.repository.AddressRepository;
import org.example.eatopia.domain.category.entity.Category;
import org.example.eatopia.domain.category.repository.CategoryRepository;
import org.example.eatopia.domain.order.entity.Order;
import org.example.eatopia.domain.order.entity.OrderDetail;
import org.example.eatopia.domain.order.enums.OrderStatus;
import org.example.eatopia.domain.order.repository.OrderDetailRepository;
import org.example.eatopia.domain.order.repository.OrderRepository;
import org.example.eatopia.domain.product.entity.Product;
import org.example.eatopia.domain.product.enums.ProductStatus;
import org.example.eatopia.domain.product.repository.ProductRepository;
import org.example.eatopia.domain.user.config.UserRole;
import org.example.eatopia.domain.user.entity.User;
import org.example.eatopia.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
public class DummyDataGeneratorTest {

    private final Random random = new Random();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    /**
     * 통계 부하 테스트를 위한 더미 데이터 10,000개 생성 (날짜 분산 적용)
     */
    @Test
    @Transactional
    @Rollback(false)
    void generateLoadTestData() {
        System.out.println("더미 데이터 생성을 시작합니다...");

        User buyer = getOrCreateUser("buyer_loadtest@test.com", "구매자(부하)", UserRole.BUYER);
        Address address = getOrCreateAddress(buyer, "부하 테스트용 주소", "12345");

        int sellerCount = 5;
        int productPerSeller = 4;
        List<Product> allProducts = new ArrayList<>();
        Category category = getOrCreateCategory();

        for (int i = 0; i < sellerCount; i++) {
            User seller = getOrCreateUser("seller_loadtest" + i + "@test.com", "판매자(부하)" + i, UserRole.SELLER);
            for (int j = 0; j < productPerSeller; j++) {
                allProducts.add(getOrCreateProduct(seller, category, "부하 테스트 상품 " + i + "-" + j, new BigDecimal(10000 + (i * 1000) + j)));
            }
        }
        System.out.println("기본 데이터 생성 완료. 총 " + allProducts.size() + "개 상품 생성.");


        // 주문 10,000개 생성
        int orderCount = 10000;
        int batchSize = 1000;
        List<Order> ordersToSave = new ArrayList<>();
        List<OrderDetail> detailsToSave = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < orderCount; i++) {
            Product product = allProducts.get(random.nextInt(allProducts.size()));
            int quantity = random.nextInt(3) + 1;
            BigDecimal price = product.getPrice();
            BigDecimal totalProductPrice = price.multiply(BigDecimal.valueOf(quantity));
            String orderCode = "LOAD-" + UUID.randomUUID().toString().substring(0, 10);

            Order order = Order.create(
                    buyer, orderCode, totalProductPrice, BigDecimal.ZERO,
                    new BigDecimal("3000"), BigDecimal.ZERO,
                    totalProductPrice.add(new BigDecimal("3000")),
                    null, address.getAddress()
            );
            order.updateStatus(OrderStatus.SUCCESS);

            int randomDaysAgo = random.nextInt(365); // 0~364일 전
            LocalDateTime randomDateTime = now.minusDays(randomDaysAgo).withHour(random.nextInt(24));
            //order.forceSetCreatedAt(randomDateTime);

            ordersToSave.add(order);

            OrderDetail detail = OrderDetail.create(order, product, quantity, price);

            //detail.forceSetCreatedAt(order.getCreatedAt());

            detailsToSave.add(detail);

            // 배치 로직
            if ((i + 1) % batchSize == 0 || (i + 1) == orderCount) {
                orderRepository.saveAll(ordersToSave);
                orderDetailRepository.saveAll(detailsToSave);

                ordersToSave.clear();
                detailsToSave.clear();

                System.out.println((i + 1) + " / " + orderCount + "개 주문 생성 완료...");
            }
        }

        System.out.println("더미 데이터 생성 완료. 총 " + orderCount + "개 주문 생성.");
    }

    // Helper Methods
    private User getOrCreateUser(String email, String name, UserRole role) {
        return userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.signUp(email, "password", name, role))
        );
    }

    private Category getOrCreateCategory() {
        Category parent = categoryRepository.save(Category.create("테스트-대분류-" + UUID.randomUUID().toString().substring(0, 8), null));
        Category child = categoryRepository.save(Category.create("테스트-소분류-" + UUID.randomUUID().toString().substring(0, 8), parent));
        return child;
    }

    private Product getOrCreateProduct(User seller, Category category, String name, BigDecimal price) {
        Product product = Product.create(
                name, "테스트 상품 설명", price, 1000,
                ProductStatus.AVAILABLE, category, seller
        );
        return productRepository.save(product);
    }

    private Address getOrCreateAddress(User user, String addr, String zip) {
        if (addressRepository.existsByUserAndAddressAndZipcode(user, addr, zip)) {
            return addressRepository.findAllByUserOrderByCreatedAtDesc(user).get(0);
        }
        return addressRepository.save(Address.create(user, addr, zip));
    }
}