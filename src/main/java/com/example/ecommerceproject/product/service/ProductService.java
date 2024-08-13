package com.example.ecommerceproject.product.service;

import com.example.ecommerceproject.product.dto.ProductDto;
import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    Page<ProductDto> findAll(ProductStatus productStatus, Pageable pageable);

    ProductDto findById(Long id);

    Product findByIdProduct(Long id);
}
