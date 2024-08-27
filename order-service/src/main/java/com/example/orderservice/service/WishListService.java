package com.example.orderservice.service;

import com.example.orderservice.dto.WishListResponseDto;
import com.example.orderservice.dto.WishListItemDto;
import com.example.orderservice.entity.WishListItem;
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

    List<WishListItem> findAllWishListItem(Long wishListId);

    void deleteId(Long wishListId);
}
