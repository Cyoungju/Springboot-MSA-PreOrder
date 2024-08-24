package com.example.orderservice.service;

import com.example.orderservice.dto.OrdersResponseDto;
import com.example.orderservice.dto.OrdersSuccessDetails;
import com.example.orderservice.dto.PaymentScreenResponseDto;
import com.example.orderservice.dto.PurchaseProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    PaymentScreenResponseDto purchaseProduct(String email, PurchaseProductDto purchaseProductDto);
    OrdersSuccessDetails attemptPaymentForProduct(PaymentScreenResponseDto paymentScreenData);

    List<OrdersResponseDto> getOrderList(String id);
    List<OrdersResponseDto> addOrders(String email, Long addressId);
    List<OrdersResponseDto> canceled(Long id, String email);
    List<OrdersResponseDto> returned(Long id, String email);

}
