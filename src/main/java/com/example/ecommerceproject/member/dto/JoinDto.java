package com.example.ecommerceproject.member.dto;


import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.member.entity.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
@Setter
public class JoinDto {

    private String username;
    private String password;
    private String email;
    private String role;

    @Builder
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(MemberRole.USER)
                .build();
    }
}
