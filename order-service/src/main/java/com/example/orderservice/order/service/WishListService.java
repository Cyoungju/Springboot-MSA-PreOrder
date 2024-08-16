package com.example.orderservice.order.service;

import com.example.orderservice.order.dto.WishListItemDto;
import com.example.orderservice.order.dto.WishListResponseDto;
import com.example.orderservice.order.entity.WishList;
import com.example.orderservice.order.entity.WishListItem;
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

    // 이메일을 통해 WishList를 찾기
    WishList findByMemberEmail(String email);

    List<WishListItem> findAllWishListItem(Long wishListId);

    void deleteId(Long wishListId);
}
