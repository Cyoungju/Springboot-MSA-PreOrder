package com.example.orderservice.kafka;

import com.example.orderservice.core.exception.CustomException;
import com.example.orderservice.dto.OrdersResponseDto;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.OrdersStatus;
import com.example.orderservice.repository.OrdersRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
public class KafkaConsumer {
    private final OrdersRepository ordersRepository;
    private final ObjectMapper objectMapper;


    @Autowired
    public KafkaConsumer(OrdersRepository ordersRepository){
        this.ordersRepository = ordersRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
    }

    @KafkaListener(topics = "order-success")
    public void changeSuccessStatus(String kafkaMessage) {

        log.info("Kafka Message : -> " + kafkaMessage);

        // 받은 데이터 역직렬화 작업
        Long orderId = null;

        try{
            // string형태로 들어온 값을 json값으로 변경하기
            orderId = objectMapper.readValue(kafkaMessage, Long.class);
        }catch (JsonProcessingException e){
            log.error("JsonProcessingException while parsing Kafka message", e);
            e.printStackTrace();
        }

        if (orderId == null) {
            log.error("Received Kafka message with null ID");
            return;
        }

        Orders orders = ordersRepository.findById(orderId).orElseThrow(
                () -> new CustomException("주문을 찾을 수 없습니다.")
        );

        log.info("Order status start: " + orders.getOrderStatus().getDesc());

        orders.changeOrderStatus(OrdersStatus.ACCEPTED);

        ordersRepository.save(orders);

        log.info("Order status updated: " + orders.getOrderStatus().getDesc());
    }

    @KafkaListener(topics = "order-fail")
    public void changeFailStatus(String kafkaMessage) {

        log.info("Kafka Message : -> " + kafkaMessage);

        // 받은 데이터 역직렬화 작업
        OrdersResponseDto ordersResponseDto = null;

        try{
            // string형태로 들어온 값을 json값으로 변경하기
            ordersResponseDto = objectMapper.readValue(kafkaMessage, OrdersResponseDto.class);
        }catch (JsonProcessingException e){
            log.error("JsonProcessingException while parsing Kafka message", e);
            e.printStackTrace();
        }

        if (ordersResponseDto == null || ordersResponseDto.getId() == null) {
            log.error("Received Kafka message with null ID");
            return;
        }

        Orders orders = ordersRepository.findById(ordersResponseDto.getId()).orElseThrow(
                () -> new CustomException("주문을 찾을 수 없습니다.")
        );

        log.info("Order status start: " + orders.getOrderStatus().getDesc());

        orders.changeOrderStatus(OrdersStatus.ACCEPTED_FAILED);

        ordersRepository.save(orders);

        log.info("Order status updated: " + orders.getOrderStatus().getDesc());
    }

}
