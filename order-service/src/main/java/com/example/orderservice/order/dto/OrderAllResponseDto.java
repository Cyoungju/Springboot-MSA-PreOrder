package com.example.orderservice.order.dto;



import com.example.orderservice.order.entity.OrdersItem;

import java.util.List;

public class OrderAllResponseDto {
    private Long id;

    private Long totalPrice;

    private List<OrdersItem> ordersItems;

}
