package com.example.ecommerceproject.member.controller;


import com.example.ecommerceproject.core.exception.CustomException;
import com.example.ecommerceproject.core.utils.ApiUtils;
import com.example.ecommerceproject.member.dto.MemberDto;
import com.example.ecommerceproject.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> joinProcess(@RequestBody @Valid MemberDto joinDto) {
        memberService.joinProcess(joinDto);
        return ResponseEntity.ok( ApiUtils.success("회원가입이 완료되었습니다!") );
    }


}
