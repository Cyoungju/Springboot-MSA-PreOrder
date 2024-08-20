package com.example.productservice.repository;

import com.example.productservice.entity.Product;
import com.example.productservice.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByProductStatus(ProductStatus productStatus, Pageable pageable);

}
