package com.example.orderservice.order.repository;
import com.example.orderservice.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem,Long> {
}
