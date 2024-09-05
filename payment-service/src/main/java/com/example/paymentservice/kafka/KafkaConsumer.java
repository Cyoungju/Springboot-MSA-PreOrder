package com.example.paymentservice.kafka;


import com.example.paymentservice.core.exception.CustomException;
import com.example.paymentservice.dto.OrdersResponseDto;
import com.example.paymentservice.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaConsumer {
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;


    @Autowired
    public KafkaConsumer(PaymentService paymentService){
        this.paymentService = paymentService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
    }

    @KafkaListener(topics = "payment-topic")
    public void listenPaymentTopic(String kafkaMessage) {
        log.info("Kafka Message : -> " + kafkaMessage);
        try {
            // 메시지를 OrdersResponseDto로 변환
            OrdersResponseDto ordersResponseDto = null;

            // 결제 처리
            ordersResponseDto = objectMapper.readValue(kafkaMessage, OrdersResponseDto.class);
            paymentService.processPayment(ordersResponseDto);

        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리 로직 추가
        }
    }

}
