package com.example.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @PostMapping("/api/client/products/{id}/decrease-stock/db")
    void updateStock(@PathVariable("id") Long productId, @RequestParam("count") int count);

    @PostMapping("/api/client/products/{id}/redis-increase-stock")
    void redisIncreaseStock(@PathVariable("id") Long productId, @RequestParam("count") int count);

}
