package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private Long productId;

    private String productName;

    private Long productPrice;

    private int productCount;

    private LocalTime availableFrom;
}
