package com.example.orderservice.controller;

import com.example.orderservice.dto.*;
import com.example.orderservice.service.OrdersService;
import com.example.orderservice.core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;

    // 결제 진입
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseProduct(
            @RequestHeader("X-Authenticated-User") String email,
            @RequestBody PurchaseProductDto purchaseProductDto){

        PaymentScreenResponseDto response = ordersService.purchaseProduct(email, purchaseProductDto);
        return  ResponseEntity.ok(response);
    }

    // 결제
    @PostMapping("/attempt-payment")
    public ResponseEntity<OrderSuccess> attemptPaymentForProduct(
            @RequestHeader("X-Authenticated-User") String email,
            @RequestHeader("X-Order-Signature") String orderSignature) {
        OrderSuccess response = ordersService.attemptPaymentForProduct(email, orderSignature);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@RequestHeader("X-Authenticated-User") String email, @PathVariable Long id){
        OrdersSuccessDetails response = ordersService.getOrderDetail(email,id);
        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestHeader("X-Authenticated-User") String email){
        List<OrdersResponseDto> ordersResponseDto = ordersService.getOrderList(email);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PostMapping
    public ResponseEntity<?> addOrder(@RequestHeader("X-Authenticated-User") String email, Long addressId){

        List<OrdersResponseDto> ordersResponseDto = ordersService.addOrders(email, addressId);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestHeader("X-Authenticated-User") String email){

        List<OrdersResponseDto> ordersResponseDto = ordersService.canceled(id, email);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PatchMapping("/return/{id}")
    public ResponseEntity<?> returnOrder(@PathVariable Long id, @RequestHeader("X-Authenticated-User") String email){

        List<OrdersResponseDto> ordersResponseDto = ordersService.returned(id, email);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }


}