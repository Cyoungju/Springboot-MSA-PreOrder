package com.example.ecommerceproject.order.service;


import com.example.ecommerceproject.core.exception.CustomException;
import com.example.ecommerceproject.order.dto.OrdersResponseDto;
import com.example.ecommerceproject.order.entity.Orders;
import com.example.ecommerceproject.order.entity.OrdersItem;
import com.example.ecommerceproject.order.entity.OrdersStatus;
import com.example.ecommerceproject.order.repository.OrdersRepository;
import com.example.ecommerceproject.order.entity.WishListItem;
import com.example.ecommerceproject.user.entity.Member;
import com.example.ecommerceproject.user.service.MemberService;
import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductRepository productRepository;


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

        // 장바구니가 비어 있는 경우 예외 처리
        if (wishListItems.isEmpty()) {
            throw new CustomException("장바구니가 비어있습니다.");
        }

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

            OrdersItem ordersItem = OrdersItem.builder()
                    .product(product)
                    .orders(orders)
                    .count(wishListItem.getCount())
                    .productPrice(product.getPrice() * wishListItem.getCount())
                    .productName(product.getName())
                    .build();

            ordersItems.add(ordersItem);

            //재고 감소
            product.decreaseStock(wishListItem.getCount());
            productRepository.save(product);
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
            orders.changeOrderStatus(OrdersStatus.CANCELED);

            // 상품의 수량을 복구
            for (OrdersItem item : orders.getOrdersItems()) {
                Product product = item.getProduct();
                product.increaseStock(item.getCount()); // 상품의 수량 복구
                productRepository.save(product); // 변경된 상품을 저장
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
