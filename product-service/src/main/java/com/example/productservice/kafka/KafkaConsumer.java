package com.example.productservice.kafka;

import com.example.productservice.dto.PaymentSuccessResponseDto;
import com.example.productservice.entity.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import com.example.productservice.service.ProductServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class KafkaConsumer {
    private final ObjectMapper objectMapper;
    private final ProductService productService;



    @Autowired
    public KafkaConsumer(ProductService productService){
        this.productService = productService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // JavaTimeModule 등록
    }

    @KafkaListener(topics = "stock-update")
    public void updateStock(String kafkaMessage){

        log.info("Kafka Message : -> " + kafkaMessage);

        try{
            List<PaymentSuccessResponseDto> productDtoList = objectMapper.readValue(kafkaMessage, new TypeReference<List<PaymentSuccessResponseDto>>(){});
            for (PaymentSuccessResponseDto productDto : productDtoList) {
                if (productDto.isSuccess()) {
                    // 결제 성공 - 데이터베이스 수량 감소
                    productService.updateStock(productDto.getId(), productDto.getCount());
                } else {
                    // 결제 실패 - Redis 수량 증가
                    productService.redisIncreaseStock(productDto.getId(), productDto.getCount());
                }
            }
        }catch (JsonProcessingException e){
            log.error("JsonProcessingException while parsing Kafka message", e);
            e.printStackTrace();
        }

    }

}
