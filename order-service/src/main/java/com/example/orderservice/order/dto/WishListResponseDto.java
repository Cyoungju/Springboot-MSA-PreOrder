package com.example.orderservice.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class WishListResponseDto {
    private List<WishListItemListDto> items;
    private Long totalPrice;
}
