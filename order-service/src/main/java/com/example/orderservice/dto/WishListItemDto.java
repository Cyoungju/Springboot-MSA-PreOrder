package com.example.orderservice.dto;


import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class WishListItemDto {

    private Long productId;

    private Long count;
}
