package com.example.ecommerceproject.member.service;

import com.example.ecommerceproject.member.entity.CustomUserDetails;
import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member memberData = memberRepository.findAllByEmail(username);
        if(memberData!= null){
            return new CustomUserDetails(memberData);
        }
        return null;
    }
}
