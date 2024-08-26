package com.example.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "PaymentScreenResponseDto", timeToLive = 1800L)
public class PaymentScreenResponseDto {
    @Id
    private String orderSignature;
    private ProductResponseDto product;
    private AddressResponseDto address;
    private Long totalPrice;
}
