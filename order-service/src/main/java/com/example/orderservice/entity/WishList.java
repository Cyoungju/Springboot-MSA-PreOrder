package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name="wishList")
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long totalPrice;

    private Long memberId;

    public void changeTotalPrice(Long totalPrice){
        this.totalPrice = totalPrice;
    }


    @Builder
    public WishList(Long id, Long totalPrice, Long memberId) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.memberId = memberId;
    }
}
