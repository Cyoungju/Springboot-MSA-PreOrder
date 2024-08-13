package com.example.ecommerceproject.orders.repository;

import com.example.ecommerceproject.orders.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem,Long> {
}
