package com.example.orderservice.repository;
import com.example.orderservice.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem,Long> {
}
