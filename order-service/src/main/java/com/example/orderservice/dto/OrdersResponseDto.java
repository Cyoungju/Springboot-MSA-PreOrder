package com.example.orderservice.dto;

import com.example.orderservice.core.utils.EncryptionUtil;
import com.example.orderservice.entity.Orders;
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

    // 배송지
    private String address;

    private String detailAdr;

    private String phone;


    public OrdersResponseDto(Orders orders) {
        this.id = orders.getId();
        this.totalPrice = orders.getTotalPrice();
        this.ordersStatus = orders.getOrderStatus().getDesc();
        this.address = orders.getAddress();
        this.detailAdr = orders.getDetailAdr();
        this.phone = orders.getPhone();
    }
}
