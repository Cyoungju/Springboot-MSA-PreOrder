package com.example.ecommerceproject.order.repository;

import com.example.ecommerceproject.order.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByMember_Email(String email);
}
