package com.example.productservice.service;

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

    Product findByIdProduct(Long id);
}
