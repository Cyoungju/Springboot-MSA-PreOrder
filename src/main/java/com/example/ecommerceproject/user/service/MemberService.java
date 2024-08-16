package com.example.ecommerceproject.user.service;

import com.example.ecommerceproject.user.dto.MemberDto;
import com.example.ecommerceproject.user.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void joinProcess(MemberDto memberDto);

    Long memberByEmail(String email);

    Member getMemberByEmail(String email);
}