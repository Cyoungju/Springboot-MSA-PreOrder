package com.example.ecommerceproject.member.service;

import com.example.ecommerceproject.member.dto.MemberDto;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    public void joinProcess(MemberDto memberDto);

}