package com.example.orderservice.service;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.core.utils.EncryptionUtil;
import com.example.orderservice.dto.*;
import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.entity.OrdersItem;
import com.example.orderservice.entity.WishListItem;
import com.example.orderservice.kafka.KafkaProducer;
import com.example.orderservice.repository.OrdersRepository;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import com.example.orderservice.repository.WishListRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;

    private final WishListService wishListService;

    private final WishListRepository wishListRepository;

    private final ProductServiceClient productServiceClient;

    private final EncryptionUtil encryptionUtil;

    private final KafkaProducer kafkaProducer;
    private static final String PAYMENT_TOPIC = "payment-topic";


    // 결제 진입 - order 생성
    @Override
    @Transactional
    public OrdersSuccess purchaseProductDirectly(String email, PurchaseProductDto purchaseProductDto) {

        // 20% 확률로 결제 시도 중 이탈 시뮬레이션
//        if (new Random().nextInt(100) < 20) {
//            throw new CustomException("결제 시도 중 고객이 이탈했습니다.");
//        }

        Long productId = purchaseProductDto.getProductId();
        int count = purchaseProductDto.getCount();
        AddressResponseDto address = purchaseProductDto.getAddress();

        // ====== 검증작업 ======
        // a. 상품 정보 조회
        ProductResponseDto product = checkProduct(productId);

        // b. 구매 가능 시간 검증
        validatePurchaseTime(product);
        // ====== 검증작업 ======


        // 1. 상품 수량 조회 - 재고확인
        checkStock(productId, count, product.getProductName());

        // 2. 주문 생성 / 주문 항목 추가 - 재고가 있을 경우에만 주문생성
        Orders orders = createOrder(email, address, product, count);

        // 주문 저장
        ordersRepository.save(orders);

        // 재고 감소 요청
        productServiceClient.decreaseStock(product.getProductId(), count);

        // 결제요청
        OrdersResponseDto ordersResponseDto = new OrdersResponseDto(
                orders.getId(),
                orders.getCreateAt(),
                orders.getOrderStatus().getDesc(),
                orders.getTotalPrice(),
                orders.getOrdersItems().stream().map(
                    item -> new OrderItemResponseDto(
                            item.getProductId(),
                            item.getCount()
                )).collect(Collectors.toList())

        );

        kafkaProducer.sendPayment(PAYMENT_TOPIC, ordersResponseDto);

        // 9. 생성된 주문 반환
        return new OrdersSuccess(
                orders.getId(),
                orders.getCreateAt(),
                orders.getOrderStatus().getDesc(),
                orders.getTotalPrice()
        );

    }

    private Orders createOrder(String email, AddressResponseDto address, ProductResponseDto product, int count) {
        // 주문 생성
        Orders orders = Orders.builder()
                .orderStatus(OrdersStatus.PAYMENT_IN_PROGRESS)
                .memberEmail(email)
                .addressName(address.getAddressName())
                .address(encryptionUtil.encrypt(address.getAddress()))
                .detailAdr(encryptionUtil.encrypt(address.getDetailAdr()))
                .phone(encryptionUtil.encrypt(address.getPhone()))
                .build();

        // 주문 항목 추가
        OrdersItem ordersItem = OrdersItem.builder()
                .productId(product.getProductId())
                .orders(orders)
                .count(count)
                .productPrice(product.getProductPrice() * count)
                .productName(product.getProductName())
                .build();

        // 주문 항목 리스트에 추가
        List<OrdersItem> ordersItems = new ArrayList<>();
        ordersItems.add(ordersItem);

        // 주문에 아이템 추가 및 총 가격 설정
        orders.changeOrderItem(ordersItems);
        orders.changeTotalPrice(ordersItem.getProductPrice());

        return orders;
    }


    // 시간 검증
    private void validatePurchaseTime(ProductResponseDto product) {
        LocalTime now = LocalTime.now();
        if (product.getAvailableFrom() != null && now.isBefore(product.getAvailableFrom())) {
            throw new CustomException("해당 상품은 " + product.getAvailableFrom() + " 이후에 구매 가능합니다.");
        }
    }


    // 수량체크 메소드
    private void checkStock(Long productId, int count, String productName){
        int productStock = getProductStock(productId);
        if (productStock < count) {
            throw new CustomException("상품 재고가 부족합니다: " + productName);
        }
    }


    // 상품체크
    private ProductResponseDto checkProduct(Long productId){
        try {
            ProductResponseDto product = getProduct(productId);
            return product;

        }catch (Exception e){
            throw new CustomException("해당 상품을 찾을수 없습니다.");

        }
    }

    // 주문 상세
    @Override
    public OrdersSuccessDetails getOrderDetail(String email, Long orderId){

        Orders orders = ordersRepository.findById(orderId).orElseThrow(
                () -> new CustomException("주문이 없습니다.")
        );

        return new OrdersSuccessDetails(
                orders.getId(),
                orders.getOrderStatus().getDesc(),
                orders.getCreateAt(),
                orders.getOrdersItems().stream().map(
                        item -> new ProductResponseDto(
                                item.getProductId(),
                                item.getProductName(),
                                item.getProductPrice(),
                                item.getCount(),
                                null
                        )).collect(Collectors.toList()),
                new AddressResponseDto(
                        orders.getAddressName(),
                        encryptionUtil.decrypt(orders.getAddress()),
                        encryptionUtil.decrypt(orders.getDetailAdr()),
                        encryptionUtil.decrypt(orders.getPhone())
                ),
                orders.getTotalPrice()
        );
    }

    // 주문 목록
    @Override
    public List<OrdersSuccess> getOrderList(String email){

        // 조회한 회원 ID를 사용하여 주문 목록 조회
        List<Orders> ordersList = ordersRepository.findByMemberEmail(email);

        return ordersList.stream()
            .map(order -> new OrdersSuccess(
                    order.getId(),
                    order.getCreateAt(),
                    order.getOrderStatus().getDesc(),
                    order.getTotalPrice()
            ))
            .collect(Collectors.toList());
    }


    // 주문 - 장바구니 상품
    @Transactional
    @Override
    public OrdersSuccess addOrders(String email, AddressResponseDto address){

        // wishList 조회
        Long wishListId = wishListRepository.findByMemberEmail(email).get().getId();
        List<WishListItem> wishListItems = wishListService.findAllWishListItem(wishListId);

        // 장바구니가 비어 있는 경우 예외 처리
        if (wishListItems.isEmpty()) {
            throw new CustomException("장바구니가 비어있습니다.");
        }

        // 주문 생성
        Orders orders = Orders.builder()
                .orderStatus(OrdersStatus.PAYMENT_IN_PROGRESS)
                .memberEmail(email)
                .addressName(address.getAddressName())
                .address(encryptionUtil.encrypt(address.getAddress()))
                .detailAdr(encryptionUtil.encrypt(address.getDetailAdr()))
                .phone(encryptionUtil.encrypt(address.getPhone()))
                .build();

        // orderItem 에저장
        // wishList객체를 기반으로 item 생성
        List<OrdersItem> ordersItems = new ArrayList<>();

        // 총 가격 계산
        Long totalPrice = 0L;

        for (WishListItem wishListItem : wishListItems) {
            // ProductService에서 제품 정보 조회
            ProductResponseDto product = checkProduct(wishListItem.getProductId());

            // 구매 가능 시간 검증
            validatePurchaseTime(product);

            // 수량 체크
            checkStock(wishListItem.getProductId(), wishListItem.getCount(), wishListItem.getProductName());
            
            // OrdersItem 생성
            OrdersItem ordersItem = OrdersItem.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .productPrice(product.getProductPrice() * wishListItem.getCount())
                    .count(wishListItem.getCount())
                    .orders(orders)
                    .build();

            ordersItems.add(ordersItem);

            // 재고 감소 요청
            productServiceClient.decreaseStock(product.getProductId(), wishListItem.getCount());

            // 총 가격 누적
            totalPrice += ordersItem.getProductPrice();
        }

        // 주문에 아이템 추가 및 총 가격 설정
        orders.changeOrderItem(ordersItems);
        orders.changeTotalPrice(totalPrice);

        // 주문 저장
        ordersRepository.save(orders);

        // WishList 비우기
        wishListService.deleteId(wishListId);

        // 결제요청
        OrdersResponseDto ordersResponseDto = new OrdersResponseDto(
                orders.getId(),
                orders.getCreateAt(),
                orders.getOrderStatus().getDesc(),
                orders.getTotalPrice(),
                orders.getOrdersItems().stream().map(
                        item -> new OrderItemResponseDto(
                                item.getProductId(),
                                item.getCount()
                        )).collect(Collectors.toList())

        );

        kafkaProducer.sendPayment(PAYMENT_TOPIC, ordersResponseDto);

        // 업데이트된 주문 목록 반환
        return new OrdersSuccess(
                orders.getId(),
                orders.getCreateAt(),
                orders.getOrderStatus().getDesc(),
                orders.getTotalPrice()
        );
    }


    // 주문 취소
    @Transactional
    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback2")
    @Retry(name = "productService",  fallbackMethod = "productServiceFallback2")
    //@TimeLimiter(name = "productService")
    public List<OrdersSuccess> canceled(Long id, String email){

        Orders orders = ordersRepository.findById(id).orElseThrow();
        if(orders.getOrderStatus() == OrdersStatus.ACCEPTED) {
            orders.changeOrderStatus(OrdersStatus.CANCELED);

            // 상품의 수량을 복구
            for (OrdersItem item : orders.getOrdersItems()) {
                ProductResponseDto product = getProduct(item.getProductId());

                // 수량 증가 - redis / DB에 stock 저장
                productServiceClient.increaseStock(product.getProductId(), item.getCount());
            }
            ordersRepository.save(orders);
            return getOrderList(email);
        }else if(orders.getOrderStatus() == OrdersStatus.CANCELED) {
            throw new CustomException("취소된 상품 입니다");
        }else {
            throw new CustomException("취소 불가한 상품 입니다");
        }

    }

    // 반품신청
    @Transactional
    @Override
    public List<OrdersSuccess> returned(Long id, String email) {
        Orders orders = ordersRepository.findById(id).orElseThrow();
        if (orders.getOrderStatus() == OrdersStatus.SHIPPED) {
            orders.changeOrderStatus(OrdersStatus.RETURN_REQUESTED);

            ordersRepository.save(orders);

            return getOrderList(email);

        }else if(orders.getOrderStatus() == OrdersStatus.RETURN_REQUESTED){
            throw new CustomException("반품 진행중인 상품 입니다");

        }else if(orders.getOrderStatus() == OrdersStatus.RETURNED) {
            throw new CustomException("반품 완료된 상품 입니다");
        }else {
            throw new CustomException("반품 불가한 상품 입니다");
        }
    }

    // 상품 조회
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    @Retry(name = "productService",  fallbackMethod = "productServiceFallback")
    //@TimeLimiter(name = "productService")
    public ProductResponseDto getProduct(Long productId) {
        return productServiceClient.getProduct(productId);
    }

    // 상품 수량 조회
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback1")
    @Retry(name = "productService",  fallbackMethod = "productServiceFallback1")
    //@TimeLimiter(name = "productService")
    public int getProductStock(Long productId){
        return productServiceClient.getStock(productId);
    }


    // 상품 조회 - 예외처리 메소드
    public ProductResponseDto productServiceFallback(Long productId, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return new ProductResponseDto();
    }

    // 상품 수량 조회 - 예외처리 메소드
    public int productServiceFallback1(Long productId, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return 0;
    }

    // 수량복구 - 예외처리 메소드
    public List<OrdersSuccess> productServiceFallback2(Long id, String email, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return Collections.emptyList();
    }


}