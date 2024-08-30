package com.example.productservice.service;

import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.entity.Product;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public interface ProductService {
    Page<ProductDto> findAll(Pageable pageable);

    ProductDto findById(Long id);

    ProductResponseDto findByIdStatusProduct(Long id);

    ProductResponseDto getProductId(Long id);

    // 재고 감소
    void decreaseStock(Long productId, int count);

    void asyncBatchUpdateStock(Long productId, int count);

    void increaseStock(Long productId, int count);

    int getStock(Long productId);

    void changeSaleTime(Long productId, LocalTime time);

}
