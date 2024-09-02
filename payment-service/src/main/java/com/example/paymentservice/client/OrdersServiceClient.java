package com.example.paymentservice.client;


import com.example.paymentservice.dto.OrdersResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service")
public interface OrdersServiceClient {

    @GetMapping("/api/client/{orderId}")
    OrdersResponseDto getOrders(@PathVariable("orderId") Long id);

    @GetMapping("/api/client/{orderId}/orderStatus/{orderStatus}")
    void changeStatus(@PathVariable("orderId") Long orderId, @PathVariable("orderStatus") int orderStatus);

}
