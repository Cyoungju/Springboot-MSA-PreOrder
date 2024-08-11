package com.example.ecommerceproject.product.service;

import com.example.ecommerceproject.core.exception.CustomException;
import com.example.ecommerceproject.product.dto.ProductDto;
import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true) // 읽기 전용
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Override
    public List<ProductDto> findAll() {

        List<Product> products = productRepository.findAll();

        if(products.isEmpty()){
            throw new CustomException("해당 상품이 비어있습니다!");
        }

        List<ProductDto> productDtos = products.stream().map(ProductDto::new).collect(Collectors.toList());

        return productDtos;
    }

    @Override
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다")
        );

        return new ProductDto(product);
    }
}
