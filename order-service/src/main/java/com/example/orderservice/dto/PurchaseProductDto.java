package com.example.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseProductDto {

    private Long productId;
    private int count;
    private AddressResponseDto address;
}
