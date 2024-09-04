package com.example.paymentservice.service;


import com.example.paymentservice.client.OrdersServiceClient;
import com.example.paymentservice.client.ProductServiceClient;
import com.example.paymentservice.core.exception.CustomException;
import com.example.paymentservice.dto.OrdersResponseDto;
import com.example.paymentservice.dto.PaymentSuccessResponseDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.kafka.KafkaProducer;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final ProductServiceClient productServiceClient;

    @Transactional
    public boolean processPayment(OrdersResponseDto ordersResponseDto){

        if (!ordersResponseDto.getOrdersStatus().equals("결제 진행 중")) {
            throw new CustomException("결제할 수 없는 상태입니다.");
        }

        // 결제 요청시 결제 정보 생성
        Payment payment = Payment.builder()
                .paymentStatus(false)
                .orderId(ordersResponseDto.getId())
                .totalPrice(ordersResponseDto.getTotalPrice())
                .build();

        boolean success = processPayment(payment);

        if (success) {
            // 결제 성공
            ordersResponseDto.getOrderItemList().forEach(product -> {
                productServiceClient.updateStock(product.getOrderItemId(), product.getOrderItemCount());
            });
            return true;

        }else {
            ordersResponseDto.getOrderItemList().forEach(product -> {
                productServiceClient.redisIncreaseStock(product.getOrderItemId(), product.getOrderItemCount());
            });

            return false;
        }
    }

    private boolean processPayment(Payment payment) {

        payment.changePaymentStatus(true);

        paymentRepository.save(payment);  // 결제 정보를 저장 (성공 또는 실패)

        return true;
    }


}
