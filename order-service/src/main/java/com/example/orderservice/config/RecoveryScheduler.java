package com.example.orderservice.config;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.dto.PaymentScreenResponseDto;
import com.example.orderservice.repository.PaymentScreenResponseDtoRepository;
import com.example.orderservice.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RecoveryScheduler {

    @Autowired
    private PaymentScreenResponseDtoRepository paymentScreenResponseRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void checkAndRecoverStock() {
        try {
            // Redis에서 모든 키를 조회
            Iterable<PaymentScreenResponseDto> paymentScreenResponseDtos = paymentScreenResponseRepository.findAll();

            for (PaymentScreenResponseDto paymentScreenResponseDto : paymentScreenResponseDtos) {
                String orderSignature = paymentScreenResponseDto.getOrderSignature();
                Long productId = paymentScreenResponseDto.getProduct().getProductId();
                int count = paymentScreenResponseDto.getProduct().getProductCount();

                // 복구 작업 수행
                productServiceClient.increaseStock(productId, count);
                System.out.println("Increased stock for productId " + productId + " by " + count);

                // 복구 작업 완료 후 Redis에서 정보 삭제
               paymentScreenResponseRepository.deleteById(orderSignature);
               System.out.println("Deleted key: " + orderSignature);
            }
        } catch (Exception e) {
            // 예외를 로깅
            System.err.println("Error during stock recovery: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
