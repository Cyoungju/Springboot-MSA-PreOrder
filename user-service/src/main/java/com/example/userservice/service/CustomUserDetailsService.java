package com.example.userservice.service;

import com.example.userservice.core.utils.EncryptionUtil;
import com.example.userservice.entity.CustomUserDetails;
import com.example.userservice.entity.Member;
import com.example.userservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final EncryptionUtil encryptionUtil;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member memberData = memberRepository.findByEmail(encryptionUtil.encrypt(username));
        if (memberData != null) {
            Hibernate.initialize(memberData.getMemberRoleList());
            //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(memberData);
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
