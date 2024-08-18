package com.example.orderservice.controller;

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

        //String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.getOrderList(email);

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
