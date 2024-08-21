package com.example.orderservice.service;

import com.example.orderservice.dto.OrdersResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrdersService {
    List<OrdersResponseDto> getOrderList(String id);
    List<OrdersResponseDto> addOrders(String email, Long addressId);
    List<OrdersResponseDto> canceled(Long id, String email);
    List<OrdersResponseDto> returned(Long id, String email);

}
