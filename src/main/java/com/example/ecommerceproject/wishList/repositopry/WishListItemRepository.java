package com.example.ecommerceproject.wishList.repositopry;

import com.example.ecommerceproject.wishList.entity.WishList;
import com.example.ecommerceproject.wishList.entity.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {
    // 특정한 사용자의 모든 장바구니 아이템을 가져올 경우
    List<WishListItem> findByWishListMemberId(Long memberId);

    Optional<WishListItem> findByWishListAndProductId(WishList wishList, Long productID);

    List<WishListItem> findByWishList(WishList wishList);
}
