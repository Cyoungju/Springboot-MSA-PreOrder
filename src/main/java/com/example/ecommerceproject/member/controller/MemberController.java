package com.example.ecommerceproject.member.controller;


import com.example.ecommerceproject.core.exception.CustomException;
import com.example.ecommerceproject.member.dto.MemberDto;
import com.example.ecommerceproject.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> joinProcess(@RequestBody @Valid MemberDto joinDto){
        memberService.joinProcess(joinDto);
        return ResponseEntity.ok(Map.of("msg", "회원가입을 완료했습니다."));
    }

}
