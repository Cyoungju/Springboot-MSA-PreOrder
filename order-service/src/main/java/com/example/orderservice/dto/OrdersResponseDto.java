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
public class OrdersResponseDto {

    private Long id;

    private Long totalPrice;

    private String ordersStatus;

    private LocalDateTime createAt;

}
