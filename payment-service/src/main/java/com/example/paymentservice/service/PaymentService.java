package com.example.paymentservice.service;


import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.entity.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public boolean processPayment(PaymentRequest paymentRequest){
        //order정보 받아오기

        // 결제 요청시 결제 정보 생성
        Payment payment = Payment.builder()
                .paymentStatus(false)
                .orderId(paymentRequest.getOrderId())
                .totalPrice(paymentRequest.getTotalPrice())
                .build();

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
