package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    PaymentScreenResponseDto purchaseProduct(String email, PurchaseProductDto purchaseProductDto);
    OrderSuccess attemptPaymentForProduct(String email, String orderSignature);

    OrdersSuccessDetails getOrderDetail(String email, Long orderId);
    List<OrdersResponseDto> getOrderList(String id);
    List<OrdersResponseDto> addOrders(String email, Long addressId);
    List<OrdersResponseDto> canceled(Long id, String email);
    List<OrdersResponseDto> returned(Long id, String email);

}