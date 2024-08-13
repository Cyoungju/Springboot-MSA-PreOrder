package com.example.ecommerceproject.orders.entity;


import com.example.ecommerceproject.core.utils.BaseTimeEntity;
import com.example.ecommerceproject.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@ToString(exclude = "member")
@Table(name="orders")
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long totalPrice;

    @Convert(converter = OrdersStatusConverter.class)
    @Column(nullable = false)
    private OrdersStatus orderStatus; // 기본값

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersItem> ordersItems = new ArrayList<>();

    public void changeOrderItem(List<OrdersItem> ordersItems) {
        this.ordersItems = ordersItems;
    }
    public void changeTotalPrice(Long totalPrice){
        this.totalPrice = totalPrice;
    }


}
