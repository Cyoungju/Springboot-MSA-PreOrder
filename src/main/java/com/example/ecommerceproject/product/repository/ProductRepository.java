package com.example.ecommerceproject.product.repository;

import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.product.entity.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Page<Product> findByProductStatus(ProductStatus productStatus, Pageable pageable);
}
