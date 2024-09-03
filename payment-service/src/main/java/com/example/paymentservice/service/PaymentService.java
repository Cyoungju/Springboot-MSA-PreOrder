package com.example.paymentservice.service;


import com.example.paymentservice.client.OrdersServiceClient;
import com.example.paymentservice.client.ProductServiceClient;
import com.example.paymentservice.core.exception.CustomException;
import com.example.paymentservice.dto.OrdersResponseDto;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrdersServiceClient ordersServiceClient;

    private final ProductServiceClient productServiceClient;

    @Transactional
    public void processPayment(Long orderId){
       // order 정보 받아오기
        OrdersResponseDto orders = ordersServiceClient.getOrders(orderId);

        System.out.println(orders.getOrdersStatus());

        if (!orders.getOrdersStatus().equals("결제 진행 중")) {
            throw new CustomException("결제할 수 없는 상태입니다.");
        }

        // 결제 요청시 결제 정보 생성
        Payment payment = Payment.builder()
                .paymentStatus(false)
                .orderId(orders.getId())
                .totalPrice(orders.getTotalPrice())
                .build();

        boolean success = processPayment(payment);


        if (success) { // order의 상태 변경 OrdersStatus.ACCEPTED, 아이템 수량 감소
            // 결제 성공
            // 성공 메시지 보내기
            // todo: kafka에 결제 성공 전송
            // order에서 상태변경
            // product에서 재고 변경
            ordersServiceClient.changeStatus(orderId,1);
            orders.getOrderItemList().forEach(product -> {
                productServiceClient.updateStock(product.getOrderItemId(), product.getOrderItemCount());
            });

        }else {  //order의상태변경 OrdersStatus.ACCEPTED_FAILED
            // todo: kafka에 결제 실패 전송
            // order에서 상태변경
            // product에서 재고 변경
            ordersServiceClient.changeStatus(orderId,8);
            orders.getOrderItemList().forEach(product -> {
                productServiceClient.redisIncreaseStock(product.getOrderItemId(), product.getOrderItemCount());
            });
        }

    }


   
    public boolean processPayment(Payment payment){

        // 20% 확률로 결제 실패 시뮬레이션
        boolean paymentSuccess = new Random().nextInt(100) >= 20;

        if (paymentSuccess) {
            //결제 성공시 paymentStatus상태 변경
            payment.changePaymentStatus(true);
            paymentRepository.save(payment);
            return true;
        } else {
            paymentRepository.save(payment);
            return false;
        }

    }


}
