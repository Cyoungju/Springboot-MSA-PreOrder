package com.example.ecommerceproject.orders.service;

import com.example.ecommerceproject.orders.dto.OrdersResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    List<OrdersResponseDto> getOrderList(String email);
    List<OrdersResponseDto> addOrders(String email);

    List<OrdersResponseDto> canceled(Long id, String email);

    List<OrdersResponseDto> returned(Long id, String email);

    List<OrdersResponseDto> processReturn(Long id, String email);
}
