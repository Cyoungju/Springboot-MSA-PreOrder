package com.example.ecommerceproject.orders.dto;


import com.example.ecommerceproject.orders.entity.Orders;
import com.example.ecommerceproject.orders.entity.OrdersStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrdersResponseDto {

    private Long id;

    private Long totalPrice;

    private String ordersStatus;

    public OrdersResponseDto(Orders orders) {
        this.id = orders.getId();
        this.totalPrice = orders.getTotalPrice();
        this.ordersStatus = orders.getOrderStatus().getDesc();
    }
}
