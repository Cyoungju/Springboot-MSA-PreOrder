package com.example.orderservice.repository;

import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByMemberEmail(String email);

    // 특정 시간 이전의 주문을 찾는 메서드
    List<Orders> findByCreateAtLessThanEqual(LocalDateTime dateTime);

    Optional<Orders> findByMemberEmailAndOrderStatus(String email, OrdersStatus ordersStatus);

}
