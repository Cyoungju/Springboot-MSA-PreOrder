package com.example.ecommerceproject.product.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 300)
    private String content;

    @Column(length = 100, nullable = false)
    private Long price;

    @Column(length = 100, nullable = false)
    private int stock;

    private ProductStatus productStatus;

    @Column(nullable = false)
    private boolean flag;

    @Builder
    public Product(Long id, String name, String content, Long price, int stock, ProductStatus productStatus, boolean flag) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.price = price;
        this.stock = stock;
        this.productStatus = productStatus;
        this.flag = flag;
    }
}
