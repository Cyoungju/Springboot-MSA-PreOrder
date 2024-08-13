package com.example.ecommerceproject.member.service;

import com.example.ecommerceproject.member.dto.MemberDto;
import com.example.ecommerceproject.member.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void joinProcess(MemberDto memberDto);

    Long memberByEmail(String email);

    Member getMemberByEmail(String email);
}