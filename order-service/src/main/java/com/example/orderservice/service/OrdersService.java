package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    OrdersResponseDto purchaseProductDirectly(String email, PurchaseProductDto purchaseProductDto);
    OrdersSuccessDetails processPayment(Long orderId);
    List<OrdersResponseDto> getOrderList(String id);
    OrdersResponseDto addOrders(String email, Long addressId);
    List<OrdersResponseDto> canceled(Long id, String email);
    List<OrdersResponseDto> returned(Long id, String email);
    OrdersSuccessDetails getOrderDetail(String email, Long id);
}