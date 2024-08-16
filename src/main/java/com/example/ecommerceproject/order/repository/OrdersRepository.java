package com.example.ecommerceproject.order.repository;

import com.example.ecommerceproject.order.entity.Orders;
import com.example.ecommerceproject.order.entity.OrdersStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByOrderStatusAndCreateAtBefore(OrdersStatus ordersStatus, LocalDateTime dateTime);

    List<Orders> findByMember_Email(String email);

    // 특정 시간 이전의 주문을 찾는 메서드
    List<Orders> findByCreateAtLessThanEqual(LocalDateTime dateTime);

}
