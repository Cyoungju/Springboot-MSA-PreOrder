package com.example.orderservice.service;

import com.example.orderservice.client.MemberServiceClient;
import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.dto.AddressResponseDto;
import com.example.orderservice.dto.MemberResponseDto;
import com.example.orderservice.dto.OrdersResponseDto;
import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.dto.ProductResponseDto;
import com.example.orderservice.entity.OrdersItem;
import com.example.orderservice.entity.WishListItem;
import com.example.orderservice.repository.OrdersRepository;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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

    private final MemberServiceClient memberServiceClient;

    private final ProductServiceClient productServiceClient;


    @Override
    public List<OrdersResponseDto> getOrderList(String email){
        // email로 member 정보 받아오기
        MemberResponseDto user = getUserByEmail(email);

        // 사용자 정보 없을때 예외 처리
        if (user == null) {
            throw new CustomException("사용자를 찾을 수 없습니다.");
        }

        // 조회한 회원 ID를 사용하여 주문 목록 조회
        List<Orders> ordersList = ordersRepository.findByMemberId(user.getId());

        return ordersList.stream()
                .map(OrdersResponseDto::new)
                .collect(Collectors.toList());
    }

    // 주문
    @Transactional
    @Override
    public List<OrdersResponseDto> addOrders(String email,Long addressId){

        // wishList 조회
        Long wishListId = wishListService.findByMemberEmail(email).getId();
        List<WishListItem> wishListItems = wishListService.findAllWishListItem(wishListId);

        // 장바구니가 비어 있는 경우 예외 처리
        if (wishListItems.isEmpty()) {
            throw new CustomException("장바구니가 비어있습니다.");
        }

        MemberResponseDto user = getUserByEmail(email);

        // 배송지 정보 가지고 오기
        AddressResponseDto address;
        if(addressId != null){
            address = memberServiceClient.getAddressById(addressId).orElseThrow(() -> new CustomException("선택한 배송지를 찾을 수 없습니다."));;
        }else{
            address = memberServiceClient.getDefaultAddress(user.getId()) .orElseThrow(() -> new CustomException("기본 배송지가 설정되어 있지 않습니다."));
        }

        // 주문생성
        // Order객체생성 - builder로
        Orders orders = Orders.builder()
                .orderStatus(OrdersStatus.ACCEPTED) // 초기 ACCEPTED
                .memberId(user.getId())
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

    @Cacheable(value="member", key = "#email")
    @CircuitBreaker(name = "memberService", fallbackMethod = "memberServiceFallback")
    public MemberResponseDto getUserByEmail(String email) {
        return memberServiceClient.getUserByEmail(email);
    }

    @Cacheable(value = "product", key = "#productId")
    @CircuitBreaker(name = "productService", fallbackMethod = "productServiceFallback")
    public ProductResponseDto getProduct(Long productId) {
        return productServiceClient.getProduct(productId);
    }


    public List<OrdersResponseDto> memberServiceFallback(String email, Throwable throwable) {
        log.error("Member Service is down: {}", throwable.getMessage());
        return Collections.emptyList();
    }

    public ProductResponseDto productServiceFallback(Long productId, Throwable throwable) {
        log.error("Product Service is down: {}", throwable.getMessage());
        return new ProductResponseDto();
    }


}
