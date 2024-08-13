package com.example.ecommerceproject.orders.controller;


import com.example.ecommerceproject.core.utils.ApiUtils;
import com.example.ecommerceproject.orders.dto.OrdersResponseDto;
import com.example.ecommerceproject.orders.service.OrdersService;
import com.example.ecommerceproject.wishList.dto.WishListItemDto;
import com.example.ecommerceproject.wishList.dto.WishListResponseDto;
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
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId, Principal principal){
        String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.canceled(orderId, username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }

    @PatchMapping("/return/{id}")
    public ResponseEntity<?> returnOrder(@PathVariable Long orderId, Principal principal){
        String username = principal.getName();

        List<OrdersResponseDto> ordersResponseDto = ordersService.returned(orderId, username);

        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(ordersResponseDto);
        return ResponseEntity.ok(apiResult);
    }
}
