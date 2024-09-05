package com.example.paymentservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PaymentSuccessResponseDto {
    private Long id;

    private int count;

    private boolean success;
}
