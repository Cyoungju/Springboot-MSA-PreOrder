package com.example.orderservice.dto;



import com.example.orderservice.entity.OrdersItem;

import java.util.List;

public class OrderAllResponseDto {
    private Long id;

    private Long totalPrice;

    private List<OrdersItem> ordersItems;

}
