package com.example.orderservice.order.controller;

import com.example.orderservice.core.utils.ApiUtils;
import com.example.orderservice.order.dto.OrdersResponseDto;
import com.example.orderservice.order.service.OrdersService;
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
    public ResponseEntity<?> getList(Principal principal){
        String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.getOrderList(username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PostMapping
    public ResponseEntity<?> addOrder(Principal principal){
        String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.addOrders(username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, Principal principal){
        String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.canceled(id, username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PatchMapping("/return/{id}")
    public ResponseEntity<?> returnOrder(@PathVariable Long id, Principal principal){
        String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.returned(id, username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }
}
