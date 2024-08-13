package com.example.ecommerceproject.wishList.entity;


import com.example.ecommerceproject.member.entity.Member;
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

    @OneToOne
    private Member member;

    public void changeTotalPrice(Long totalPrice){
        this.totalPrice = totalPrice;
    }


    @Builder
    public WishList(Long id, Long totalPrice, Member member) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.member = member;
    }
}
