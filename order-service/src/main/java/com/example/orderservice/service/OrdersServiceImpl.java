package com.example.orderservice.service;

import com.example.orderservice.client.MemberServiceClient;
import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.core.utils.EncryptionUtil;
import com.example.orderservice.dto.*;
import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.entity.OrdersItem;
import com.example.orderservice.entity.WishListItem;
import com.example.orderservice.repository.OrdersItemRepository;
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
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class OrdersServiceImpl implements OrdersService {
    private final OrdersItemRepository ordersItemRepository;

    private final OrdersRepository ordersRepository;

    private final WishListService wishListService;

    private final WishListRepository wishListRepository;

    private final MemberServiceClient memberServiceClient;

    private final ProductServiceClient productServiceClient;

    private final EncryptionUtil encryptionUtil;

    private final RedisService redisService;


    // 결제 진입 API
    @Override
    public PaymentScreenResponseDto purchaseProduct(String email, PurchaseProductDto purchaseProductDto){

        // 상품정보 조회
        ProductResponseDto product = getProduct(purchaseProductDto.getProductId());

        int count = purchaseProductDto.getCount();

        // 수량체크
        if(product.getProductStock() < count){
            throw new CustomException("상품 재고가 부족합니다: " + product.getProductName());
        }

        // 재고 감소 요청
        productServiceClient.decreaseStock(product.getProductId(), count);

        // 랜덤한 orderSignature 생성
        String orderSignature = email+":"+UUID.randomUUID().toString();


        try {
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

            PaymentScreenResponseDto paymentScreenResponseDto = new PaymentScreenResponseDto(
                    orderSignature,
                    new ProductResponseDto(
                            product.getProductId(),
                            product.getProductName(),
                            product.getProductPrice(),
                            product.getProductStock(),
                            count
                    ),
                    decryptedAddress,
                    product.getProductPrice() * count
            );
            // 결제 정보 redis에 저장 - 30분
            redisService.saveOrderInfo(paymentScreenResponseDto);

            // 결제 화면에서 보여줄 정보 반환
            return paymentScreenResponseDto;

        }catch (Exception e){
            // 결제 실패 시 재고 복구
            productServiceClient.increaseStock(product.getProductId(), count);

            throw new CustomException("결제 완료에 실패했습니다. 다시 시도해 주세요.");
        }
    }

    //결제 API
    //결제 시도
    @Override
    @Transactional
    @CircuitBreaker(name = "productService", fallbackMethod = "productServicePaymentsFallback1")
    @Retry(name = "productService", fallbackMethod = "productServicePaymentsFallback1")
    public OrderSuccess attemptPaymentForProduct(String email, String orderSignature) {
        //redis에 저장된 결제 정보 조회
        PaymentScreenResponseDto paymentScreenData = redisService.getPurchaseProduct(orderSignature);


        if (paymentScreenData == null || !paymentScreenData.getOrderSignature().equals(orderSignature)) {
            throw new CustomException("결제 시도 정보가 유효하지 않습니다.");
        }

        // 결제 시도 정보가 유효하면 진행
        try {
            Long productId = paymentScreenData.getProduct().getProductId();
            int count = paymentScreenData.getProduct().getProductCount();

            // 수량체크 - 재고확인
            ProductResponseDto product = getProduct(productId);
//            if (product.getProductStock() < count) {
//                throw new CustomException("결제 시점에 재고가 부족합니다: " + product.getProductName());
//            }
            // 주문 생성
            Orders orders = Orders.builder()
                    .orderStatus(OrdersStatus.ACCEPTED) // 초기 상태
                    .memberEmail(email)
                    .address(encryptionUtil.encrypt(paymentScreenData.getAddress().getAddress()))
                    .detailAdr(encryptionUtil.encrypt(paymentScreenData.getAddress().getDetailAdr()))
                    .phone(encryptionUtil.encrypt(paymentScreenData.getAddress().getPhone()))
                    .build();

            OrdersItem ordersItem = OrdersItem.builder()
                    .productId(productId)
                    .orders(orders)
                    .count(count)
                    .productPrice(product.getProductPrice() * count)
                    .productName(product.getProductName())
                    .build();

            orders.changeOrderItem(Collections.singletonList(ordersItem));
            orders.changeTotalPrice(ordersItem.getProductPrice());


            // 결제 완료 후 Redis에서 정보 삭제
            redisService.deletePurchaseProduct(orderSignature);


            // 결제 완료 처리
            return completePaymentForProduct(orders, paymentScreenData);
        } catch (Exception e) {
            log.error("결제 시도 중 오류 발생: {}", e.getMessage());

            // 주문 상태를 ACCEPTED_FAILED로 업데이트
            Orders orders = ordersRepository.findByMemberEmailAndOrderStatus(email, OrdersStatus.ACCEPTED)
                    .orElseThrow(() -> new CustomException("주문을 찾을 수 없습니다."));
            orders.changeOrderStatus(OrdersStatus.ACCEPTED_FAILED);
            ordersRepository.save(orders);

            throw new CustomException("결제 시도에 실패했습니다. 다시 시도해 주세요.");

        }
    }


    // 결제 완료
    @Transactional
    @CircuitBreaker(name = "productService", fallbackMethod = "productServicePaymentsFallback2")
    @Retry(name = "productService", fallbackMethod = "productServicePaymentsFallback2")
    @TimeLimiter(name = "productService")
    public OrderSuccess completePaymentForProduct(Orders orders, PaymentScreenResponseDto paymentScreenData) {

        // 선택한 상품 정보 조회
        ProductResponseDto product = paymentScreenData.getProduct();
        int count =paymentScreenData.getProduct().getProductCount();

        try {
            // 주문에 아이템 추가 및 총 가격 설정
            orders.changeOrderStatus(OrdersStatus.ACCEPTED); // 결제 완료 상태
            orders.changeTotalPrice(product.getProductPrice() * count);


            // 주문 저장
            ordersRepository.save(orders);

            // 주문 완료된 정보 반환
            return
                    new OrderSuccess(
                            orders.getId(),
                            orders.getCreateAt(),
                            orders.getOrderStatus().getDesc()
                    );

        }catch (Exception e){
            orders.changeOrderStatus(OrdersStatus.ACCEPTED_FAILED);
            ordersRepository.save(orders);
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
                        order.getCreateAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public OrdersSuccessDetails getOrderDetail(String email, Long orderId){

        Orders orders = ordersRepository.findById(orderId).orElseThrow(
                () -> new CustomException("주문이 없습니다.")
        );

        // OrdersItem 클래스가 productId를 가지고 있다고 가정합니다.
        List<OrdersItem> ordersItems = ordersItemRepository.findByOrdersId(orderId);

        // 각 OrdersItem에서 productId를 추출하고, 해당 ID로 ProductResponseDto를 가져옵니다.
        List<ProductResponseDto> products = ordersItems.stream()
                .map(item -> new ProductResponseDto(
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductPrice(),
                        getProduct(item.getProductId()).getProductStock(),
                        item.getCount()
                ))
                .collect(Collectors.toList());


        AddressResponseDto address = getAddress(orders.getAddressId(),email);

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

        return
                new OrdersSuccessDetails(
                        orders.getId(),
                        orders.getOrderStatus().getDesc(),
                        orders.getCreateAt(),
                        products,
                        decryptedAddress,
                        orders.getTotalPrice()
                );
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
                .addressId(addressId)
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
                    .productPrice(product.getProductPrice())
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

    public OrderSuccess productServicePaymentsFallback1(String email, String orderSignature, Throwable throwable) {

        return new OrderSuccess();
    }

    public OrderSuccess productServicePaymentsFallback2(Orders orders, PaymentScreenResponseDto paymentScreenData, Throwable throwable) {

        return new OrderSuccess();
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