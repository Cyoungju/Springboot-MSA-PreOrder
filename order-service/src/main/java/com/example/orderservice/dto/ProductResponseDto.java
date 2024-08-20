package com.example.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponseDto {

    private Long productId;

    private String productName;

    private Long productPrice;

    private int productStock;
}
