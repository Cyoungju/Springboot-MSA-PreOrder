package com.example.paymentservice.controller;

import com.example.paymentservice.core.utils.ApiUtils;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<?> payment(@PathVariable Long orderId){
        paymentService.processPayment(orderId);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success("결제가 완료되었습니다. 주문번호 : " + orderId);
        return ResponseEntity.ok(apiResult);
    }

}
