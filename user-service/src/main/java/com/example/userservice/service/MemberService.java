package com.example.userservice.service;

import com.example.userservice.dto.MemberDto;
import com.example.userservice.dto.MemberResponseDto;
import com.example.userservice.entity.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    void joinProcess(MemberDto memberDto);

    Long memberByEmail(String email);

    Member getMemberByEmail(String email);

    MemberDto getMemberDetailsByEmail(String username);

    MemberResponseDto getUserByEmail(String email);
}