package com.example.paymentservice.controller;

import com.example.paymentservice.dto.OrdersResponseDto;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments/process")
    public boolean processPayment(@RequestBody OrdersResponseDto ordersResponseDto) {
        return paymentService.processPayment(ordersResponseDto);
    }

}
