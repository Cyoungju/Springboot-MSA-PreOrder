package com.example.orderservice.entity;

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
@ToString(exclude = "ordersItems")
@Table(name="orders")
public class Orders extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long totalPrice;

    @Convert(converter = OrdersStatusConverter.class)
    @Column(nullable = false)
    private OrdersStatus orderStatus; // 기본값

    private Long memberId;

    // 배송지
    private String address;

    private String detailAdr;

    private String phone;

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
