package com.example.orderservice.client;

import com.example.orderservice.dto.OrdersResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    @PostMapping("/payments/process")
    boolean processPayment(@RequestBody OrdersResponseDto ordersResponseDto);
}
