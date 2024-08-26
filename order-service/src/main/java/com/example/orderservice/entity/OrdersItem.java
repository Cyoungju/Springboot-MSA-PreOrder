package com.example.orderservice.entity;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@ToString(exclude = {"orders"})
@Table(name="orderItem", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"productId", "orders_id"})
})
public class OrdersItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long productId;

    private String productName;

    private Long productPrice;

    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Orders orders;

}
