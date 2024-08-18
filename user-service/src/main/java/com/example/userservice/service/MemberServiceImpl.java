package com.example.userservice.service;

import com.example.userservice.core.exception.CustomException;
import com.example.userservice.core.utils.EncryptionUtil;
import com.example.userservice.dto.MemberDto;
import com.example.userservice.entity.Member;
import com.example.userservice.dto.VerificationCodeStore;
import com.example.userservice.entity.MemberRole;
import com.example.userservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionUtil encryptionUtil;
    private final VerificationCodeStore verificationCodeStore;

    @Transactional
    @Override
    public void joinProcess(MemberDto memberDto){

        // 이메일 암호화
        String originalEmail = memberDto.getEmail();
        String encryptedEmail = encryptionUtil.encrypt(originalEmail);

        // 암호화된 이메일과 데이터베이스의 이메일 비교
        Member existingMember = memberRepository.findByEmail(encryptedEmail);
        boolean existEmail = (existingMember != null);


        //이미 존재하는 이메일인 경우
        if(existEmail){
            throw new CustomException("이미 존재하는 아이디입니다!");
        }

        //이메일 인증 확인 - 아닐경우 에러 메시지
        // ==============================================
        // throw new CustomException("이메일 인증 해주세요!");
        // 통과 된다면 회원가입 진행
        // 이메일 인증 확인 - 아닐경우 에러 메시지
        //verificationCodeStore.getVerificationStatus(originalEmail); // 인증 상태를 확인

        // ==============================================

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(memberDto.getPassword());

        Member member = Member.builder()
                .email(encryptedEmail)
                .password(encodedPassword)
                .username(encryptionUtil.encrypt(memberDto.getUsername()))
                .phone(encryptionUtil.encrypt(memberDto.getPhone()))
                .address(encryptionUtil.encrypt(memberDto.getAddress()))
                .detailAdr(encryptionUtil.encrypt(memberDto.getDetailAdr()))
                .build();

        // 기본 사용자 - defalt
        member.addRole(MemberRole.USER);

        memberRepository.save(member);
        // 인증 번호 삭제
        //verificationCodeStore.removeCode(originalEmail);

    }

    @Override
    public Long memberByEmail(String email) {
        Member member = memberRepository.findByEmail(email);

        Long memberId = member.getId();

        return memberId;
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public MemberDto getMemberDetailsByEmail(String username) {
        Member member = memberRepository.findByEmail(username);
        MemberDto memberDto = new MemberDto();
        return memberDto.updateDTO(member);
    }
}
