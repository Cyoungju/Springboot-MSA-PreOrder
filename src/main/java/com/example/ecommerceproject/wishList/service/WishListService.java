package com.example.ecommerceproject.wishList.service;

import com.example.ecommerceproject.wishList.dto.WishListItemDto;
import com.example.ecommerceproject.wishList.dto.WishListItemListDto;
import com.example.ecommerceproject.wishList.dto.WishListResponseDto;
import com.example.ecommerceproject.wishList.entity.WishList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WishListService {
    // 장바구니 리스트 조회
    WishListResponseDto wishList(String email);

    // 장바구니 상품 추가및 수량변경 - 상품에서 버튼을 눌렀을때
    WishListResponseDto addWishList(WishListItemDto wishListItemDto, String email);

    // 상품 삭제 - 장바구니에서 삭제버튼 클릭
    WishListResponseDto deleteWishListItem(Long wishListId, String email);
}
