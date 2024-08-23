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

    private String memberEmail;

    //private Long memberId;

    public void changeTotalPrice(Long totalPrice){
        this.totalPrice = totalPrice;
    }


    @Builder
    public WishList(Long id, Long totalPrice, String memberEmail) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.memberEmail = memberEmail;
    }
}
