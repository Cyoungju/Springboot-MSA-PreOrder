package com.example.orderservice.repository;
import com.example.orderservice.entity.Orders;
import com.example.orderservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    Optional<WishList> findByMemberEmail(String email);
}
