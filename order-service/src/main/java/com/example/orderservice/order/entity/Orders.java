package com.example.orderservice.order.entity;

import com.example.orderservice.core.utils.BaseTimeEntity;
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

    // TODO: Member API 요청
    //@ManyToOne(fetch = FetchType.LAZY)
    //private Member member;

    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrdersItem> ordersItems = new ArrayList<>();

    public void changeOrderItem(List<OrdersItem> ordersItems) {
        this.ordersItems = ordersItems;
    }
    public void changeTotalPrice(Long totalPrice){
        this.totalPrice = totalPrice;
    }
    public void changeOrderStatus(OrdersStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

}
