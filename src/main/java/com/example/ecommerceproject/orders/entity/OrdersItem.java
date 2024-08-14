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
@Table(name="orderItem", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"orders_id", "product_id"})})
public class OrdersItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Long productPrice;

    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public void reQuantity() {
        product.increaseStock(count);
    }
}
