package com.example.orderservice.service;

import com.example.orderservice.client.MemberServiceClient;
import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.core.utils.EncryptionUtil;
import com.example.orderservice.dto.*;
import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.entity.OrdersItem;
import com.example.orderservice.entity.WishListItem;
import com.example.orderservice.repository.OrdersRepository;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import com.example.orderservice.repository.WishListRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;

    private final WishListService wishListService;

    private final WishListRepository wishListRepository;

    private final MemberServiceClient memberServiceClient;

    private final ProductServiceClient productServiceClient;

    private final EncryptionUtil encryptionUtil;


    // 결제 진입 - order 생성
    @Override
    @Transactional
    public OrdersResponseDto purchaseProductDirectly(String email, PurchaseProductDto purchaseProductDto){
        Long productId = purchaseProductDto.getProductId();
        int count = purchaseProductDto.getCount();
        AddressResponseDto address = purchaseProductDto.getAddress();

        // 상품 정보 조회
        ProductResponseDto product = getProduct(productId);

        int productStock = getProductStock(productId);


        // 재고확인
        if (productStock < count) {
            throw new CustomException("상품 재고가 부족합니다: " + product.getProductName());
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

        // 주문 저장
        ordersRepository.save(orders);

        // 재고 감소 요청
        productServiceClient.decreaseStock(product.getProductId(), count);

        // 9. 생성된 주문 반환
        return new OrdersResponseDto(
                orders.getId(),
                orders.getCreateAt(),
                orders.getOrderStatus().getDesc(),
                orders.getTotalPrice()
        );
    }

    // 결제 진행
    @Override
    @Transactional
    public OrdersSuccessDetails processPayment(Long orderId){
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("주문을 찾을 수 없습니다."));

        if (orders.getOrderStatus() != OrdersStatus.PAYMENT_IN_PROGRESS) {
            throw new CustomException("결제할 수 없는 상태입니다.");
        }

        // 20% 확률로 결제 실패 시뮬레이션
        //boolean paymentSuccess = new Random().nextInt(100) >= 20;

        //if (paymentSuccess) {
            orders.changeOrderStatus(OrdersStatus.ACCEPTED);
            ordersRepository.save(orders);
            // 비동기 재고 업데이트 호출
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (OrdersItem item : orders.getOrdersItems()) {
                    productServiceClient.asyncBatchUpdateStock(item.getProductId(), item.getCount());
                }
            });
            log.info("결제 성공: 주문 ID = {}", orderId);

            // 비동기 작업이 완료될 때까지 기다리거나 상태를 확인
            future.join();
        //} else {
            //throw new CustomException("결제가 실패했습니다. 다시 시도해 주세요.");
        //}

        return new OrdersSuccessDetails(
                orders.getId(),
                orders.getOrderStatus().getDesc(),
                orders.getCreateAt(),
                orders.getOrdersItems().stream().map(
                        item -> new ProductResponseDto(
                                item.getProductId(),
                                item.getProductName(),
                                item.getProductPrice(),
                                item.getCount()
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
                                item.getCount()
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


    @Override
    public List<OrdersResponseDto> getOrderList(String email){

        // 조회한 회원 ID를 사용하여 주문 목록 조회
        List<Orders> ordersList = ordersRepository.findByMemberEmail(email);

        return ordersList.stream()
            .map(order -> new OrdersResponseDto(
                    order.getId(),
                    order.getCreateAt(),
                    order.getOrderStatus().getDesc(),
                    order.getTotalPrice()
            ))
            .collect(Collectors.toList());
    }


    // 주문
    @Transactional
    @Override
    @CircuitBreaker(name = "userService", fallbackMethod = "memberServiceFallback")
    public OrdersResponseDto addOrders(String email,Long addressId){

        // wishList 조회
        Long wishListId = wishListRepository.findByMemberEmail(email).get().getId();
        List<WishListItem> wishListItems = wishListService.findAllWishListItem(wishListId);

        // 장바구니가 비어 있는 경우 예외 처리
        if (wishListItems.isEmpty()) {
            throw new CustomException("장바구니가 비어있습니다.");
        }

        // 배송지 정보 가지고 오기
        AddressResponseDto address;
        if(addressId != null){
            address = memberServiceClient.getAddressById(addressId).orElseThrow(() -> new CustomException("선택한 배송지를 찾을 수 없습니다."));;
        }else{
            address = memberServiceClient.getDefaultAddress(email) .orElseThrow(() -> new CustomException("기본 배송지가 설정되어 있지 않습니다."));
        }

        // 주문생성
        // Order객체생성 - builder로
        Orders orders = Orders.builder()
                .orderStatus(OrdersStatus.ACCEPTED) // 초기 ACCEPTED
                .memberEmail(email)
                .address(address.getAddress())
                .detailAdr(address.getDetailAdr())
                .phone(address.getPhone())
                .build();

        // orderItem 에저장
        // wishList객체를 기반으로 item 생성
        List<OrdersItem> ordersItems = new ArrayList<>();

        // 총 가격 계산
        Long totalPrice = 0L;

        for (WishListItem wishListItem : wishListItems) {
            // ProductService에서 제품 정보 조회
            ProductResponseDto product = getProduct(wishListItem.getProductId());

            // 재고 확인
            if (productServiceClient.getStock(product.getProductId()) < wishListItem.getCount()) {
                throw new CustomException("상품 재고가 부족합니다: " + product.getProductName());
            }
            // OrdersItem 생성
            OrdersItem ordersItem = OrdersItem.builder()
                    .productId(product.getProductId())
                    .orders(orders)
                    .count(wishListItem.getCount())
                    .productPrice(product.getProductPrice() * wishListItem.getCount())
                    .productName(product.getProductName())
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

        // 업데이트된 주문 목록 반환
        return new OrdersResponseDto(
                orders.getId(),
                orders.getCreateAt(),
                orders.getOrderStatus().getDesc(),
                orders.getTotalPrice()
        );
    }


    // 주문 취소
    @Transactional
    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    public List<OrdersResponseDto> canceled(Long id, String email){

        Orders orders = ordersRepository.findById(id).orElseThrow();
        if(orders.getOrderStatus() == OrdersStatus.ACCEPTED) {
            orders.changeOrderStatus(OrdersStatus.CANCELED);

            // 상품의 수량을 복구
            for (OrdersItem item : orders.getOrdersItems()) {
                ProductResponseDto product = getProduct(item.getProductId());

                // 수량 증가 - 저장 까지
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
    public List<OrdersResponseDto> returned(Long id, String email) {
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


    //@Cacheable(value = "product", key = "#productId")
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    public ProductResponseDto getProduct(Long productId) {
        return productServiceClient.getProduct(productId);
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback1")
    public int getProductStock(Long productId){
        return productServiceClient.getStock(productId);
    }


    public List<OrdersResponseDto> memberServiceFallback(String email, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return Collections.emptyList();
    }

    public OrdersResponseDto memberServiceFallback1(String email, PurchaseProductDto purchaseProductDto, Throwable throwable) {
        log.error("Member Service is down: {}", throwable.getMessage());
        return new OrdersResponseDto();
    }


    public ProductResponseDto productServiceFallback(Long productId, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return new ProductResponseDto();
    }

    public int productServiceFallback1(Long productId, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return 0;
    }


}