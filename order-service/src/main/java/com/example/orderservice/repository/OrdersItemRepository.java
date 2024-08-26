package com.example.orderservice.repository;
import com.example.orderservice.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersItemRepository extends JpaRepository<OrdersItem,Long> {
    List<OrdersItem> findByOrdersId(Long orderId);
}
