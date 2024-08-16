package com.example.orderservice.repository;
import com.example.orderservice.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    // TODO: Member API 요청
    //Optional<WishList> findByMember_Email(String email);
}
