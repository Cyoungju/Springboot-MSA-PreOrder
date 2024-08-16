package com.example.ecommerceproject.user.repository;

import com.example.ecommerceproject.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String email);

}
