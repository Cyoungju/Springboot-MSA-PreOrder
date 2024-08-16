package com.example.productservice.product.service;

import com.example.productservice.product.dto.ProductDto;
import com.example.productservice.product.entity.Product;
import com.example.productservice.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {
    Page<ProductDto> findAll(ProductStatus productStatus, Pageable pageable);

    ProductDto findById(Long id);

    Product findByIdProduct(Long id);
}
