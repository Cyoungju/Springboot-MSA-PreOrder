package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class OrdersSuccess {
    private Long id;

    private LocalDateTime createAt;

    private String ordersStatus;

    private Long totalPrice;
}
