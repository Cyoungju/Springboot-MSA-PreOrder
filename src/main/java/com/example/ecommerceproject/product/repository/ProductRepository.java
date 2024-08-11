package com.example.ecommerceproject.product.repository;

import com.example.ecommerceproject.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
