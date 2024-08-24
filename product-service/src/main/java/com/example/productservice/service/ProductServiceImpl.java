package com.example.productservice.service;

import com.example.productservice.core.exception.CustomException;
import com.example.productservice.dto.ProductResponseDto;
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
    public Page<ProductDto> findAll(Pageable pageable) {

        Page<ProductDto> productsDto = findByStatusNot(pageable);

        System.out.println();

        if(productsDto.isEmpty()){
            throw new CustomException("해당 상품이 비어있습니다!");
        }

        return productsDto;
    }

    private Page<ProductDto> findByStatusNot(Pageable pageable){
        Page<Product> products = productRepository.findByStatusNot(ProductStatus.DISCONTINUED, pageable);
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
    public ProductResponseDto findByIdStatusProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다.")
        );

        if(product.getProductStatus() == ProductStatus.AVAILABLE){
            return new ProductResponseDto(product.getId(), product.getName(), product.getPrice(), product.getStock());

        }else {
            throw new CustomException("해당 상품은 현재 구매할수 없습니다.");
        }
    }

    @Override
    public ProductResponseDto getProductId(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다.")
        );
        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }

    // 제품 재고 감소
    @Override
    public void decreaseStock(Long productId, int count) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
        if (product.getStock() < count) {
            throw new RuntimeException("재고가 부족합니다.");
        }
        product.decreaseStock(count);
        productRepository.save(product);
    }

    @Override
    public void increaseStock(Long productId, int count) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
        product.increaseStock(count);
        productRepository.save(product);
    }
}
