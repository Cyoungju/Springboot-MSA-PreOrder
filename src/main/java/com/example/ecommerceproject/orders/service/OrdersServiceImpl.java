package com.example.ecommerceproject.orders.service;


import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.member.service.MemberService;
import com.example.ecommerceproject.orders.dto.OrdersResponseDto;
import com.example.ecommerceproject.orders.entity.Orders;
import com.example.ecommerceproject.orders.entity.OrdersItem;
import com.example.ecommerceproject.orders.entity.OrdersStatus;
import com.example.ecommerceproject.orders.repository.OrdersRepository;
import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.wishList.entity.WishListItem;
import com.example.ecommerceproject.wishList.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Transactional
@Service
public class OrdersServiceImpl implements OrdersService{

    private final OrdersRepository ordersRepository;
    private final WishListService wishListService;
    private final MemberService memberService;


    @Override
    public List<OrdersResponseDto> getOrderList(String email){

        List<Orders> ordersList = ordersRepository.findByMember_Email(email);

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

        Member member = memberService.getMemberByEmail(email);

        // 주문생성
        // Order객체생성 - builder로
        Orders orders = Orders.builder()
                .orderStatus(OrdersStatus.ACCEPTED) // 초기 ACCEPTED
                .member(member)
                .build();

        // orderItem 에저장
        // wishList객체를 기반으로 item 생성
        List<OrdersItem> ordersItems = new ArrayList<>();

        for (WishListItem wishListItem : wishListItems) {
            Product product = wishListItem.getProduct();

            // 기존에 같은 상품이 있는지 확인하고, 중복이 없다면 추가
            OrdersItem existingItem = ordersItems.stream()
                    .filter(item -> item.getProduct().equals(product))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                OrdersItem ordersItem = OrdersItem.builder()
                        .count(existingItem.getCount() + wishListItem.getCount())
                        .productPrice(product.getPrice() * wishListItem.getCount())
                        .productName(product.getName())
                        .build();
            } else {
                OrdersItem ordersItem = OrdersItem.builder()
                        .product(product)
                        .orders(orders)
                        .count(wishListItem.getCount())
                        .productPrice(product.getPrice() * wishListItem.getCount())
                        .productName(product.getName())
                        .build();

                ordersItems.add(ordersItem);
            }
        }

        orders.changeOrderItem(ordersItems);

        // TotalPrice
        Long totalPrice = ordersItems.stream().mapToLong(
                ordersItem -> ordersItem.getProduct().getPrice() * ordersItem.getCount()
        ).sum();
        orders.changeTotalPrice(totalPrice);

        ordersRepository.save(orders);

        // 카트의 상품 전부 삭제
        wishListService.deleteId(wishListId);

        return getOrderList(email);
    }


    // 주문 취소
    @Override
    public List<OrdersResponseDto> canceled(Long id, String email){

        Orders orders = ordersRepository.findById(id).orElseThrow();
        if(orders.getOrderStatus() == OrdersStatus.ACCEPTED) {
            orders = Orders.builder()
                    .orderStatus(OrdersStatus.CANCELED)
                    .build();

            ordersRepository.save(orders);
        }
        return getOrderList(email);
    }

    // 반품신청
    @Override
    public List<OrdersResponseDto> returned(Long id, String email) {
        Orders orders = ordersRepository.findById(id).orElseThrow();
        if (orders.getOrderStatus() == OrdersStatus.SHIPPED) {
            orders = Orders.builder()
                    .orderStatus(OrdersStatus.RETURN_REQUESTED)
                    .build();

            ordersRepository.save(orders);
        }
        return getOrderList(email);
    }

    // 반품완료
    @Override
    public List<OrdersResponseDto> processReturn(Long id, String email) {
        Orders orders = ordersRepository.findById(id).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        if (orders.getModifyAt().isBefore(now.minusDays(1))) {
            //reflectReturnInStock(orders);
            if (orders.getOrderStatus() == OrdersStatus.RETURN_REQUESTED) {
                orders = Orders.builder()
                        .orderStatus(OrdersStatus.RETURNED)
                        .build();

                ordersRepository.save(orders);
            }
        }
        return getOrderList(email);
    }


    // 주문 상태를 자동으로 업데이트
    @Scheduled(cron = "0 0 * * * *") // 매시간 정각에 실행
    public void updateOrderStatues(){

        LocalDateTime now = LocalDateTime.now();
        // 1일 경과 배송중
        List<Orders> ordersToShip = ordersRepository.findByOrderStatusAndCreateAtBefore(
                OrdersStatus.ACCEPTED,now.minusDays(1)
        );

        for(Orders orders : ordersToShip){
            orders = Orders.builder()
                    .orderStatus(OrdersStatus.ON_DELIVERY)
                    .build();

            ordersRepository.save(orders);

        }

        // 2일경과 배송완료
        List<Orders> ordersToDelivery = ordersRepository.findByOrderStatusAndCreateAtBefore(
                OrdersStatus.ON_DELIVERY, now.minusDays(2)
        );

        for(Orders orders : ordersToDelivery){
            orders = Orders.builder()
                    .orderStatus(OrdersStatus.SHIPPED)
                    .build();

            ordersRepository.save(orders);

        }

        // 3일째 확정
        List<Orders> ordersToConfirmed = ordersRepository.findByOrderStatusAndCreateAtBefore(
                OrdersStatus.SHIPPED, now.minusDays(3)
        );
        for(Orders orders : ordersToConfirmed){
            orders = Orders.builder()
                    .orderStatus(OrdersStatus.CONFIRMED)
                    .build();

            ordersRepository.save(orders);

        }

    }

}
