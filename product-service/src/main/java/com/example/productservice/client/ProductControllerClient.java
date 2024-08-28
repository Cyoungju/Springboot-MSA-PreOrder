package com.example.productservice.client;

import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class ProductControllerClient {

    private final ProductService productService;

    @GetMapping("/{productId}")
    ProductResponseDto getProduct(@PathVariable("productId") Long id){
        return productService.getProductId(id);
    }

    @GetMapping("/status/{productId}")
    ProductResponseDto findByIdStatusProduct(@PathVariable("productId") Long id){
        return productService.findByIdStatusProduct(id);
    }

    // 재고 감소
    @PostMapping("/products/{id}/decrease-stock")
    public void decreaseStock(@PathVariable("id") Long productId, @RequestParam("count") int count) {
        productService.decreaseStock(productId, count);
    }

    // 재고 증가
    @PostMapping("/products/{id}/increase-stock")
    public void increaseStock(@PathVariable("id") Long productId, @RequestParam("count") int count) {
        productService.increaseStock(productId, count);
    }

    @PostMapping("/products/{id}")
    public int getStock(@PathVariable("id") Long productId){
        return productService.getStock(productId);
    }

}
