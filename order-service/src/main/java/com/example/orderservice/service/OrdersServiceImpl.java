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
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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


    // 결제 진입 API
    @Override
    public PaymentScreenResponseDto purchaseProduct(String email, PurchaseProductDto purchaseProductDto){
        // 상품정보 조회
        ProductResponseDto product = getProduct(purchaseProductDto.getProductId());

        Long count = purchaseProductDto.getCount();

        // 수량체크
        if(product.getProductStock() < count){
            throw new CustomException("상품 재고가 부족합니다: " + product.getProductName());
        }
        // 배송지 정보 가지고 오기
        AddressResponseDto address = getAddress(purchaseProductDto.getAddressId(), email);

        // 주소 정보 복호화
        String decodedAddress = encryptionUtil.decrypt(address.getAddress());
        String decodedDetailAdr = encryptionUtil.decrypt(address.getDetailAdr());
        String decodedPhone = encryptionUtil.decrypt(address.getPhone());

        // 복호화된 주소 정보를 새로운 AddressResponseDto에 저장
        AddressResponseDto decryptedAddress = new AddressResponseDto(
                address.getAddressName(),
                decodedAddress,
                decodedDetailAdr,
                decodedPhone
        );

        // 결제 화면에서 보여줄 정보 반환
        return new PaymentScreenResponseDto(
                product,
                decryptedAddress,
                email,
                count,
                product.getProductPrice() * count
        );
    }

    //결제 API
    //결제 시도
    @Override
    @Transactional
    @CircuitBreaker(name = "productService", fallbackMethod = "productServicePaymentsFallback")
    @Retry(name = "productService", fallbackMethod = "productServicePaymentsFallback")
    public OrdersSuccessDetails attemptPaymentForProduct(PaymentScreenResponseDto paymentScreenData){
        try {
            Long count = paymentScreenData.getCount();

            // 수량체크
            ProductResponseDto product = getProduct(paymentScreenData.getProduct().getProductId());
            if (product.getProductStock() < count) {
                throw new CustomException("결제 시점에 재고가 부족합니다: " + product.getProductName());
            }

            // order생성
            return completePaymentForProduct(paymentScreenData);
        }catch (Exception e){
            log.error("결제 시도 중 오류 발생: {}", e.getMessage());
            throw new CustomException("결제 시도에 실패했습니다. 다시 시도해 주세요.");

        }
    }

    // 결제 완료
    @Transactional
    @CircuitBreaker(name = "productService", fallbackMethod = "productServicePaymentsFallback")
    @Retry(name = "productService", fallbackMethod = "productServicePaymentsFallback")
    @TimeLimiter(name = "productService")
    public OrdersSuccessDetails completePaymentForProduct(PaymentScreenResponseDto paymentScreenData) {

        // 선택한 상품 정보 조회
        ProductResponseDto product = paymentScreenData.getProduct();
        AddressResponseDto address = paymentScreenData.getAddress();
        String email = paymentScreenData.getEmail();
        Long count =paymentScreenData.getCount();

        try {
            // 주문 생성
            Orders orders = Orders.builder()
                    .orderStatus(OrdersStatus.ACCEPTED) // 결제 완료 상태
                    .memberEmail(email)
                    .address(encryptionUtil.encrypt(address.getAddress()))
                    .detailAdr(encryptionUtil.encrypt(address.getDetailAdr()))
                    .phone(encryptionUtil.encrypt(address.getPhone()))
                    .build();

            // OrdersItem 생성
            OrdersItem ordersItem = OrdersItem.builder()
                    .productId(product.getProductId())
                    .orders(orders)
                    .count(count)
                    .productPrice(product.getProductPrice() * count)
                    .productName(product.getProductName())
                    .build();

            // 주문에 아이템 추가 및 총 가격 설정
            orders.changeOrderItem(Collections.singletonList(ordersItem));
            orders.changeTotalPrice(ordersItem.getProductPrice());

            // 재고 감소 요청
            productServiceClient.decreaseStock(product.getProductId(), count);

            // 주문 저장
            ordersRepository.save(orders);

            // 주문 완료된 정보 반환
            return new OrdersSuccessDetails(
                    orders.getId(),
                    orders.getOrderStatus().getDesc(),
                    orders.getCreateAt(),
                    product,
                    address,
                    orders.getMemberEmail(),
                    (long) orders.getOrdersItems().size(),
                    orders.getTotalPrice()
            );
        }catch (Exception e){
            // 결제 실패 시 재고 복구
            productServiceClient.increaseStock(product.getProductId(), count);

            throw new CustomException("결제 완료에 실패했습니다. 다시 시도해 주세요.");
        }
    }



    @Override
    public List<OrdersResponseDto> getOrderList(String email){

        // 조회한 회원 ID를 사용하여 주문 목록 조회
        List<Orders> ordersList = ordersRepository.findByMemberEmail(email);

        return ordersList.stream()
                .map(order -> new OrdersResponseDto(
                        order.getId(),
                        order.getTotalPrice(),
                        order.getOrderStatus().getDesc(),
                        encryptionUtil.decrypt(order.getAddress()),
                        encryptionUtil.decrypt(order.getDetailAdr()),
                        encryptionUtil.decrypt(order.getPhone())
                ))
                .collect(Collectors.toList());
    }

    // 주문
    @Transactional
    @Override
    public List<OrdersResponseDto> addOrders(String email,Long addressId){

        // wishList 조회
        Long wishListId = wishListRepository.findByMemberEmail(email).get().getId();
        List<WishListItem> wishListItems = wishListService.findAllWishListItem(wishListId);

        // 장바구니가 비어 있는 경우 예외 처리
        if (wishListItems.isEmpty()) {
            throw new CustomException("장바구니가 비어있습니다.");
        }

        // 배송지 정보 가지고 오기
        AddressResponseDto address = getAddress(addressId, email);

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
            if (product.getProductStock() < wishListItem.getCount()) {
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
        return getOrderList(email);
    }


    // 주문 취소
    @Transactional
    @Override
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    @Retry(name = "productService", fallbackMethod = "productServiceFallback")
    @TimeLimiter(name = "productService")
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

    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    @Retry(name = "productService", fallbackMethod = "productServiceFallback")
    @TimeLimiter(name = "productService")
    public ProductResponseDto getProduct(Long productId) {
        try{
            return productServiceClient.getProduct(productId);
        }catch (FeignException e) {
            log.error("Product not found for ID {}: {}", productId, e.getMessage());
            throw new CustomException("해당 상품을 찾을 수 없습니다.");
        }

    }

    @CircuitBreaker(name = "userService", fallbackMethod = "memberServiceFallback")
    @Retry(name = "userService",  fallbackMethod = "memberServiceFallback")
    @TimeLimiter(name = "userService")
    public AddressResponseDto getAddress(Long addressId, String email) {
        try{
            if (addressId != null) {
                return memberServiceClient.getAddressById(addressId)
                        .orElseThrow(() -> new CustomException("선택한 배송지를 찾을 수 없습니다."));
            } else {
                return memberServiceClient.getDefaultAddress(email)
                        .orElseThrow(() -> new CustomException("기본 배송지가 설정되어 있지 않습니다."));
            }
        }catch (FeignException e) {
            throw new CustomException("기본 배송지를 찾을수 없습니다.");
        }
    }

    public OrdersSuccessDetails productServicePaymentsFallback(PaymentScreenResponseDto paymentScreenData, Throwable throwable) {

        return new OrdersSuccessDetails();
    }

    public AddressResponseDto memberServiceFallback(Long addressId, String email, Throwable throwable) {
        log.error("Member Service is down: {}", throwable.getMessage());
        return new AddressResponseDto();
    }

    public ProductResponseDto productServiceFallback(Long productId, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return new ProductResponseDto();
    }


}
