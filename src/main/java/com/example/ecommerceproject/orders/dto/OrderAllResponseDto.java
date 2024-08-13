package com.example.ecommerceproject.orders.dto;

import com.example.ecommerceproject.orders.entity.OrdersItem;
import com.example.ecommerceproject.orders.entity.OrdersStatus;

import java.util.List;

public class OrderAllResponseDto {
    private Long id;

    private Long totalPrice;

    private List<OrdersItem> ordersItems;

}
