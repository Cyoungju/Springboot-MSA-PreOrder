package com.example.ecommerceproject.product.dto;


import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.product.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String name;

    private String content;

    private Long price;

    private int stock;

    private ProductStatus productStatus;

    private boolean flag;

    public ProductDto(Product product) {
        this.name = product.getName();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.productStatus = product.getProductStatus();
        this.flag = product.isFlag();
    }
}
