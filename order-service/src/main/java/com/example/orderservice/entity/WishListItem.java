package com.example.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name="wishListItem")
@ToString(exclude = {"wishList"})
public class WishListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;

    private Long productPrice;

    private Long productId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishList_id")
    private WishList wishList;

    // 수량
    private Long count;

    public void changeCount(Long count){
        this.count = count;
    }

}
