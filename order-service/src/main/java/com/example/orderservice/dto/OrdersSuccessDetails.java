package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrdersSuccessDetails {
    private Long ordersId;

    private String ordersStatus;

    private LocalDateTime ordersCreateAt;

    private List<ProductResponseDto> product; // 상품 정보

    private AddressResponseDto address; // 배송 주소 정보

    private Long totalPrice; // 총 결제 금액

}
