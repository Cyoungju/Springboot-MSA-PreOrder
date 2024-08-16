package com.example.ecommerceproject.order.dto;

import com.example.ecommerceproject.order.entity.OrdersItem;

import java.util.List;

public class OrderAllResponseDto {
    private Long id;

    private Long totalPrice;

    private List<OrdersItem> ordersItems;

}
