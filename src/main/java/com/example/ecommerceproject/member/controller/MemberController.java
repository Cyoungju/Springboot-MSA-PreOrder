package com.example.ecommerceproject.member.controller;


import com.example.ecommerceproject.member.dto.JoinDto;
import com.example.ecommerceproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("sign-up")
    public String joinProcess(JoinDto joinDto){
        memberService.joinProcess(joinDto);
        return "ok";
    }

}
