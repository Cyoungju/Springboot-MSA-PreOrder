package com.example.paymentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrdersResponseDto {
    private Long id;

    private LocalDateTime createAt;

    private String ordersStatus;

    private Long totalPrice;

    private List<OrderItemResponseDto> orderItemList;
}
