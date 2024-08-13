package com.example.ecommerceproject.wishList.entity;


import com.example.ecommerceproject.product.entity.Product;
import com.example.ecommerceproject.wishList.entity.WishList;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name="wishListItem")
@ToString(exclude = {"wishList","product"})
public class WishListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishList_id")
    private WishList wishList;

    // 수량
    private Long count;

    public void changeCount(Long count){
        this.count = count;
    }

}
