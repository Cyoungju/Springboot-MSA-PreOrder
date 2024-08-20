package com.example.userservice.client;

import com.example.userservice.dto.MemberResponseDto;
import com.example.userservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class MemberControllerClient {

    private final MemberService memberService;

    // 이메일로 회원 정보 조회
    @GetMapping("/{email}")
    public MemberResponseDto getUserByEmail(@PathVariable("email") String email) {
        return memberService.getUserByEmail(email);
    }
}
