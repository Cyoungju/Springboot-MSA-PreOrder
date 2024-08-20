package com.example.orderservice.service;

import com.example.orderservice.client.MemberServiceClient;
import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.core.utils.EncryptionUtil;
import com.example.orderservice.dto.MemberResponseDto;
import com.example.orderservice.dto.OrdersResponseDto;
import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.dto.ProductResponseDto;
import com.example.orderservice.entity.OrdersItem;
import com.example.orderservice.entity.WishListItem;
import com.example.orderservice.repository.OrdersRepository;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional
@Service
public class OrdersServiceImpl implements OrdersService {

    private final OrdersRepository ordersRepository;
    
    private final WishListService wishListService;

    private final MemberServiceClient memberServiceClient;

    private final ProductServiceClient productServiceClient;

    @Override
    public List<OrdersResponseDto> getOrderList(String email){

        MemberResponseDto user = memberServiceClient.getUserByEmail(email);

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
    @Override
    public List<OrdersResponseDto> addOrders(String email){

        // wishList 조회
        Long wishListId = wishListService.findByMemberEmail(email).getId();
        List<WishListItem> wishListItems = wishListService.findAllWishListItem(wishListId);

        // 장바구니가 비어 있는 경우 예외 처리
        if (wishListItems.isEmpty()) {
            throw new CustomException("장바구니가 비어있습니다.");
        }

        MemberResponseDto user = memberServiceClient.getUserByEmail(email); 

        // 주문생성
        // Order객체생성 - builder로
        Orders orders = Orders.builder()
                .orderStatus(OrdersStatus.ACCEPTED) // 초기 ACCEPTED
                .memberId(user.getId())
                .build();

        // orderItem 에저장
        // wishList객체를 기반으로 item 생성
        List<OrdersItem> ordersItems = new ArrayList<>();

        // 총 가격 계산
        Long totalPrice = 0L;

        for (WishListItem wishListItem : wishListItems) {
            // ProductService에서 제품 정보 조회
            ProductResponseDto product = productServiceClient.getProduct(wishListItem.getProductId());

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
    @Override
    public List<OrdersResponseDto> canceled(Long id, String email){

        Orders orders = ordersRepository.findById(id).orElseThrow();
        if(orders.getOrderStatus() == OrdersStatus.ACCEPTED) {
            orders.changeOrderStatus(OrdersStatus.CANCELED);

            // 상품의 수량을 복구
            for (OrdersItem item : orders.getOrdersItems()) {
                ProductResponseDto product = productServiceClient.getProduct(item.getProductId());

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

}
