package com.example.ecommerceproject.wishList.repositopry;

import com.example.ecommerceproject.wishList.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByMember_Email(String email);
}
