package com.example.productservice.service;

import com.example.productservice.core.exception.CustomException;
import com.example.productservice.entity.Product;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.ProductStatus;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional(readOnly = true) // 읽기 전용
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductDto> findAll(ProductStatus productStatus, Pageable pageable) {

        Page<ProductDto> productsDto = findByProductStatus(productStatus, pageable);

        if(productsDto.isEmpty()){
            throw new CustomException("해당 상품이 비어있습니다!");
        }

        return productsDto;
    }

    private Page<ProductDto> findByProductStatus(ProductStatus productStatus, Pageable pageable){
        Page<Product> products = productRepository.findByProductStatus(productStatus, pageable);
        return products.map(ProductDto::new);
    }

    @Override
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다")
        );

        return new ProductDto(product);
    }

    @Override
    public Product findByIdProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다.")
        );

        if(product.getProductStatus() == ProductStatus.AVAILABLE){
            return product;
        }else {
            throw new CustomException("해당 상품은 현재 구매할수 없습니다.");
        }
    }
}
