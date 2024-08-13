package com.example.ecommerceproject.orders.entity;


import com.example.ecommerceproject.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@ToString(exclude = {"orders","product"})
@Table(name="orderItem")
public class OrdersItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Long productPrice;

    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    private Orders orders;

    @OneToOne
    private Product product;
}
