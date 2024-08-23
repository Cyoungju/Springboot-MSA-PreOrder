package com.example.orderservice.repository;

import com.example.orderservice.entity.WishList;
import com.example.orderservice.entity.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {

    Optional<WishListItem> findByWishListAndProductId(WishList wishList, Long productID);

    List<WishListItem> findByWishList(WishList wishList);

    List<WishListItem> findByWishListId(Long wishListId);

    @Transactional
    @Modifying // 상태변경 쿼리
    @Query("DELETE FROM WishListItem wl WHERE wl.wishList.id = :wishListId")
    void deleteByWishListId(Long wishListId);

    // 특정한 사용자의 모든 장바구니 아이템을 가져올 경우
    List<WishListItem> findByWishListMemberEmail(String email);
}
