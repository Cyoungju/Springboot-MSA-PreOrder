package com.example.orderservice.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class WishListItemListDto {
    private Long wishListItemId;

    private Long count;

    private String productName;

    private Long price;


    public WishListItemListDto(Long wishListItemId, Long count, String productName, Long price) {
        this.wishListItemId = wishListItemId;
        this.count = count;
        this.productName = productName;
        this.price = price;
    }
}
