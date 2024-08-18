package com.example.orderservice.repository;

import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders,Long> {

    List<Orders> findByOrderStatusAndCreateAtBefore(OrdersStatus ordersStatus, LocalDateTime dateTime);

    // TODO: Member API 요청
    List<Orders> findByMemberId(Long memberId);

    // 특정 시간 이전의 주문을 찾는 메서드
    List<Orders> findByCreateAtLessThanEqual(LocalDateTime dateTime);

}
