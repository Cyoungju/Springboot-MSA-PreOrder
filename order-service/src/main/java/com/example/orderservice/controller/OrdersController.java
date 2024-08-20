package com.example.orderservice.controller;

import com.example.orderservice.core.utils.EncryptionUtil;
import com.example.orderservice.dto.OrdersResponseDto;
import com.example.orderservice.service.OrdersService;
import com.example.orderservice.core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final OrdersService ordersService;


    @GetMapping
    public ResponseEntity<?> getList(@RequestHeader("X-Authenticated-User") String email){
        List<OrdersResponseDto> ordersResponseDto = ordersService.getOrderList(email);
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PostMapping
    public ResponseEntity<?> addOrder(@RequestHeader("X-Authenticated-User") String email){

        List<OrdersResponseDto> ordersResponseDto = ordersService.addOrders(email);

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
