package com.example.productservice.entity;

import com.example.productservice.core.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Entity
@Setter
@Table(name = "product")
public class Product extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 1000)
    private String content;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private int stock;

    @Convert(converter = ProductStatusConverter.class)
    @Column(nullable = false)
    private ProductStatus productStatus; // 기본값 AVAILABLE

    private LocalTime availableFrom;

    @Builder
    public Product(Long id, String name, String content, Long price, int stock, ProductStatus productStatus) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.price = price;
        this.stock = stock;
        this.productStatus = productStatus;
    }

    public void setStock(int count){
        this.stock = count;
    }

    public void changeSaleTime(LocalTime time){
        this.availableFrom = time;
    }

}
