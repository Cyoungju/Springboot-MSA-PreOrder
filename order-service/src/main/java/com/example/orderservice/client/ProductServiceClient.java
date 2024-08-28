package com.example.orderservice.client;

import com.example.orderservice.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/api/client/{productId}")
    ProductResponseDto getProduct(@PathVariable("productId") Long id);

    @GetMapping("/api/client/status/{productId}")
    ProductResponseDto findByIdStatusProduct(@PathVariable("productId") Long id);

    @PostMapping("/api/client/products/{id}/decrease-stock")
    void decreaseStock(@PathVariable("id") Long productId, @RequestParam("count") int count);

    @PostMapping("/api/client/products/{id}/increase-stock")
    void increaseStock(@PathVariable("id") Long productId, @RequestParam("count") int count);

    @PostMapping("/api/client/products/{id}")
    int getStock(@PathVariable("id") Long productId);

}
