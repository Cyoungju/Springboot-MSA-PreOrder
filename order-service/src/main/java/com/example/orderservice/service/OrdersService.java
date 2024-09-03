package com.example.orderservice.service;

import com.example.orderservice.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    OrdersSuccess purchaseProductDirectly(String email, PurchaseProductDto purchaseProductDto);
    //OrdersSuccessDetails processPayment(Long orderId);
    List<OrdersSuccess> getOrderList(String id);
    OrdersSuccess addOrders(String email, AddressResponseDto address);
    List<OrdersSuccess> canceled(Long id, String email);
    List<OrdersSuccess> returned(Long id, String email);
    OrdersSuccessDetails getOrderDetail(String email, Long id);
    OrdersResponseDto getOrder(Long id);

    void changeStatus(Long orderId, int orderStatus);
}