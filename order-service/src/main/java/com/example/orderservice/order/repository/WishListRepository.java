package com.example.orderservice.order.repository;
import com.example.orderservice.order.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    // TODO: Member API 요청
    //Optional<WishList> findByMember_Email(String email);
}
