package com.example.ecommerceproject.product.service;

import com.example.ecommerceproject.product.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<ProductDto> findAll();

    ProductDto findById(Long id);
}
