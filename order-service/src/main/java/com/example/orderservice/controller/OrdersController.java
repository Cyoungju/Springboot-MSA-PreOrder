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

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestHeader("X-Authenticated-User") String email, @RequestBody PurchaseProductDto purchaseProductDto){
        OrdersResponseDto ordersResponseDto = ordersService.purchaseProductDirectly(email, purchaseProductDto);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PostMapping("/payment/{orderId}")
    public ResponseEntity<?> payment(@PathVariable Long orderId){
        OrdersSuccessDetails ordersResponseDto = ordersService.processPayment(orderId);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
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

        OrdersResponseDto ordersResponseDto = ordersService.addOrders(email, addressId);

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