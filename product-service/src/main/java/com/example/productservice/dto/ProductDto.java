package com.example.productservice.dto;


import com.example.productservice.entity.Product;
import com.example.productservice.entity.ProductStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;

    @Size(max = 1000)
    private String content;

    @NotEmpty
    @Positive // 가격은 0보다 커야하기 때문에 설정
    private Long price;

    @Min(0)
    private int stock;

    @NotEmpty
    private ProductStatus productStatus; // 상품의 상태


    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.stock = product.getStock();
        this.productStatus = product.getProductStatus();
    }
}
