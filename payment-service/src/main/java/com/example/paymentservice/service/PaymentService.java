package com.example.paymentservice.service;


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

    private final KafkaProducer kafkaProducer;

    private static final String ORDER_SUCCESS_TOPIC = "order-success";
    private static final String ORDER_FAIL_TOPIC = "order-fail";
    private static final String STOCK_UPDATE_TOPIC = "stock-update";


    @Transactional
    public void processPayment(OrdersResponseDto ordersResponseDto){

        Long orderId = ordersResponseDto.getId();

        if (!ordersResponseDto.getOrdersStatus().equals("결제 진행 중")) {
            throw new CustomException("결제할 수 없는 상태입니다.");
        }

        boolean success = createPayment(ordersResponseDto);

        handlePaymentResult(success, orderId, ordersResponseDto);
    }

    private void handlePaymentResult(boolean success, Long orderId, OrdersResponseDto orders) {
        if (success) {
            kafkaProducer.sendOrderSuccess(ORDER_SUCCESS_TOPIC, orderId);
        } else {
            kafkaProducer.sendOrderFail(ORDER_FAIL_TOPIC, orderId);
        }
        sendStockUpdateRequests(orders, success);
    }

    private void sendStockUpdateRequests(OrdersResponseDto orders, boolean success) {
        List<PaymentSuccessResponseDto> updates = orders.getOrderItemList().stream()
                .map(product -> new PaymentSuccessResponseDto(product.getOrderItemId(), product.getOrderItemCount(), success))
                .collect(Collectors.toList());
        kafkaProducer.sendBatchStockUpdate(STOCK_UPDATE_TOPIC, updates);
    }



    public boolean createPayment(OrdersResponseDto ordersResponseDto) {
        // 결제 요청시 결제 정보 생성
        Payment payment = Payment.builder()
                .paymentStatus(false)
                .orderId(ordersResponseDto.getId())
                .totalPrice(ordersResponseDto.getTotalPrice())
                .build();
        // 20% 확률로 결제 실패 시뮬레이션
        //boolean paymentSuccess = new Random().nextInt(100) >= 20;

        //if (paymentSuccess) {
            // 결제 성공 시 paymentStatus 상태 변경
            payment.changePaymentStatus(true);
        //}
        paymentRepository.save(payment);  // 결제 정보를 저장 (성공 또는 실패)

        return true;
    }


}
