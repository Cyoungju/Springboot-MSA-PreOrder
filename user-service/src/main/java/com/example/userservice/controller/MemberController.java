package com.example.userservice.controller;

import com.example.userservice.core.utils.ApiUtils;
import com.example.userservice.dto.MemberDto;
import com.example.userservice.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
