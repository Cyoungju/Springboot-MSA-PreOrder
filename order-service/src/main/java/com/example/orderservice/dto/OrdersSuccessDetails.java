package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersSuccessDetails {
    private Long orderId;

    private String ordersStatus;

    private LocalDateTime paymentDate;

    private ProductResponseDto product; // 상품 정보

    private AddressResponseDto address; // 배송 주소 정보

    private String email; // 사용자 이메일

    private Long quantity; // 수량

    private Long totalPrice; // 총 결제 금액

}
