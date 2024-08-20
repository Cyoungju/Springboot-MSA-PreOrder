package com.example.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ProductResponseDto {

    private Long productId;

    private String productName;

    private Long productPrice;

    private int productStock;

}
