package com.example.orderservice.kafka;

import com.example.orderservice.dto.OrdersResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KafkaProducer {

    private KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;

    }

    public void sendPayment(String topic, OrdersResponseDto ordersResponseDto) {
        sendMessage(topic, ordersResponseDto);
    }
    private void sendMessage(String topic, Object data) {
        String jsonInString = "";
        try {
            jsonInString = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data: {}", jsonInString);
    }

}
