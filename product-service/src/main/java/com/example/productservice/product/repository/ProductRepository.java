package com.example.productservice.product.repository;

import com.example.productservice.product.entity.Product;
import com.example.productservice.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByProductStatus(ProductStatus productStatus, Pageable pageable);
}
