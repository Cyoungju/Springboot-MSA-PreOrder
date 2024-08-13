package com.example.ecommerceproject.wishList.dto;


import com.example.ecommerceproject.product.entity.Product;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class WishListItemDto {

    private Long productId;

    private Long count;
}
