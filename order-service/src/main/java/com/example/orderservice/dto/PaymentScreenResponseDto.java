package com.example.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentScreenResponseDto {
    private ProductResponseDto product;
    private AddressResponseDto address;
    private String email;
    private Long count;
    private Long totalPrice;
}
