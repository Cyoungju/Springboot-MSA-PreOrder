package com.example.orderservice.service;

import com.example.orderservice.dto.PaymentScreenResponseDto;
import com.example.orderservice.repository.PaymentScreenResponseDtoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final PaymentScreenResponseDtoRepository paymentScreenResponseDtoRepository;


    public void saveOrderInfo(PaymentScreenResponseDto paymentScreenResponseDto) {
        paymentScreenResponseDtoRepository.save(paymentScreenResponseDto);
    }

    public PaymentScreenResponseDto getPurchaseProduct(String id) {
        return paymentScreenResponseDtoRepository.findById(id).orElse(null);
    }

    public void deletePurchaseProduct(String id) {
        paymentScreenResponseDtoRepository.deleteById(id);
    }
}