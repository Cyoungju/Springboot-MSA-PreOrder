package com.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
public class ProductResponseDto {

    private Long productId;

    private String productName;

    private Long productPrice;

    private int productStock;

    private LocalTime availableFrom;

}
