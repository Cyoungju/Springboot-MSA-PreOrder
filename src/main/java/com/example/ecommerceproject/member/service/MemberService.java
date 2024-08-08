package com.example.ecommerceproject.member.service;

import com.example.ecommerceproject.member.dto.JoinDto;
import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void joinProcess(JoinDto joinDto){
        Optional<Member> existEmail = memberRepository.findByEmail(joinDto.getEmail());

        //이미 존재하는 이메일인 경우
        if(existEmail.isPresent()){
            try {
                throw new Exception("이미 존재하는 아이디 입니다!");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String encodedPassword = passwordEncoder.encode(joinDto.getPassword());
        joinDto.setPassword(encodedPassword);

        memberRepository.save(joinDto.toEntity());
    }
}