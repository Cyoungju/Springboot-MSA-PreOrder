package com.example.orderservice.client;


import com.example.orderservice.dto.OrdersResponseDto;
import com.example.orderservice.dto.OrdersSuccess;
import com.example.orderservice.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class PaymentControllerClient {

    private final OrdersService ordersService;

    @GetMapping("/{orderId}")
    public OrdersResponseDto getOrders(@PathVariable Long orderId){
        return ordersService.getOrder(orderId);
    }

    @GetMapping("/{orderId}/orderStatus/{orderStatus}")
    public void changeStatus(@PathVariable("orderId") Long orderId, @PathVariable("orderStatus") int orderStatus){
        ordersService.changeStatus(orderId, orderStatus);
    }
}
