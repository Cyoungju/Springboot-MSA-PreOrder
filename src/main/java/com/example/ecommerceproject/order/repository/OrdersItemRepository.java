package com.example.ecommerceproject.order.repository;

import com.example.ecommerceproject.order.entity.OrdersItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersItemRepository extends JpaRepository<OrdersItem,Long> {
}
