package com.example.productservice.service;

import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.entity.Product;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    Page<ProductDto> findAll(ProductStatus productStatus, Pageable pageable);

    ProductDto findById(Long id);

    ProductResponseDto findByIdStatusProduct(Long id);

    ProductResponseDto getProductId(Long id);

    // 재고 감소
    void decreaseStock(Long productId, int count);

    void increaseStock(Long productId, int count);
}
