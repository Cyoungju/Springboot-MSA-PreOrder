package com.example.productservice.service;

import com.example.productservice.core.exception.CustomException;
import com.example.productservice.dto.ProductResponseDto;
import com.example.productservice.entity.Product;
import com.example.productservice.dto.ProductDto;
import com.example.productservice.entity.ProductStatus;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;


@Transactional(readOnly = true) // 읽기 전용
@RequiredArgsConstructor
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final RedisTemplate<String, Integer> redisTemplate;

    private final ProductRepository productRepository;

    private final RedissonClient redissonClient;

    private static final String PRODUCT_KEY_PREFIX = "product:stock:";

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

        return products.map(
                item -> new ProductDto(
                   item.getId(),
                   item.getName(),
                   item.getContent(),
                   item.getPrice(),
                   getStock(item.getId()),
                   item.getProductStatus().getDesc()
                ));

    }

    @Override
    public ProductDto findById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다")
        );

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getContent(),
                product.getPrice(),
                getStock(product.getId()),
                product.getProductStatus().getDesc()
        );
    }

    @Override
    public ProductResponseDto findByIdStatusProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다.")
        );

        if(product.getProductStatus() == ProductStatus.AVAILABLE){
            return new ProductResponseDto(product.getId(), product.getName(), product.getPrice(), getStock(product.getId()), product.getAvailableFrom());

        }else {
            throw new CustomException("해당 상품은 현재 구매할수 없습니다.");
        }
    }

    @Override
    public ProductResponseDto getProductId(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException("해당 상품을 찾을수 없습니다.")
        );
        return new ProductResponseDto(product.getId(), product.getName(), product.getPrice(), product.getStock(), product.getAvailableFrom());
    }

    // 레디스에서만 재고 감소
    @Override
    public void decreaseStock(Long productId, int count) {
        String redisKey = PRODUCT_KEY_PREFIX + productId.toString();

        RLock lock = redissonClient.getLock("lock:" + redisKey);

        lock.lock(); // 락을 걸어 동시성 문제를 방지

        try {
            // Redis에서 현재 재고 조회
            Integer currentStock = getStock(productId);

            if (currentStock < count) {
                throw new RuntimeException("재고가 부족합니다.");
            }

            // Redis에서 재고 감소
            redisTemplate.opsForValue().set(redisKey, currentStock - count);

        }finally {
            lock.unlock();

        }
    }

    @Transactional
    public void updateStock(Long productId, int count) {

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다. 제품 ID: " + productId));

        // 재고 감소
        int newStock = product.getStock() - count;

        if (newStock < 0) {
            throw new RuntimeException("재고가 부족합니다. 제품 ID: " + productId);
        }
        product.setStock(newStock);

        productRepository.save(product);

    }


    
    // redis , DB에서 재고 증가
    @Override
    @Transactional
    public void increaseStock(Long productId, int count) {
        String redisKey = PRODUCT_KEY_PREFIX + productId.toString();

        // Redis에서 현재 재고 조회
        Integer currentStock = getStock(productId);

        // 데이터베이스에서 재고 증가
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
        product.setStock(product.getStock() + count);
        productRepository.save(product);

        // Redis에서 재고 증가
        redisTemplate.opsForValue().set(redisKey, currentStock + count);
    }


    @Override
    public void redisIncreaseStock(Long productId, int count) {
        String redisKey = PRODUCT_KEY_PREFIX + productId.toString();

        // Redis에서 현재 재고 조회
        Integer currentStock = getStock(productId);

        // Redis에서 재고 증가
        redisTemplate.opsForValue().set(redisKey, currentStock + count);
    }

    // 재고 조회
    @Override
    public int getStock(Long productId){
        String redisKey = PRODUCT_KEY_PREFIX + productId.toString();

        // 레디스에서 정보 조회
        Integer stock = redisTemplate.opsForValue().get(redisKey);


        if(stock == null){
            // 레디스에 재고 정보 캐싱
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("제품을 찾을 수 없습니다."));
            stock = product.getStock();

            // 레디스에 재고 정보 캐싱 (키값, 재고)
            redisTemplate.opsForValue().set(redisKey,stock);
        }
        return stock;
    }

    @Transactional
    @Override
    public void changeSaleTime(Long productId, LocalTime time){
        Product product = productRepository.getReferenceById(productId);

        product.changeSaleTime(time);
        productRepository.save(product);
    }
}

