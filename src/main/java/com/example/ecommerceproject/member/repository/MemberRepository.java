package com.example.ecommerceproject.member.repository;

import com.example.ecommerceproject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Member findByEmail(String email);

}
