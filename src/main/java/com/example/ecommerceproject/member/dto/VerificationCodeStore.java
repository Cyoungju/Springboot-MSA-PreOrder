package com.example.ecommerceproject.member.dto;

import com.example.ecommerceproject.core.exception.CustomException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class VerificationCodeStore {
    // ConcurrentMap java에서 제공하는 스레드 안전한 해시 맵
    // 여러 스래드가 동시에 접근하더라도 안전하게 동작할 수 있음
    // 추후 레디스도 생각해볼수 있음
    private final ConcurrentMap<String, Integer> verificationCodes = new ConcurrentHashMap<>();
    // 인증 여부 확인하는 코드
    private final ConcurrentMap<String, Boolean> emailVerificationStatus = new ConcurrentHashMap<>();


    public void storeCode(String email, int code) {
        verificationCodes.put(email, code);
        // 이메일 인증 상태를 기본값 false로 설정
        emailVerificationStatus.put(email, false);
    }

    public Integer getCode(String email) {
        return verificationCodes.get(email);
    }

    // 인증 상태를 설정
    public void setVerificationStatus(String email, boolean status) {
        emailVerificationStatus.put(email, status);
    }

    // 인증 상태를 조회
    public Boolean getVerificationStatus(String email) {
        Boolean status = emailVerificationStatus.get(email);
        if (status == null) {
            throw new CustomException("이메일 인증 해주세요!");
        }
        return status;
    }

    // 인증 코드 제거
    public void removeCode(String email) {
        verificationCodes.remove(email);
        emailVerificationStatus.remove(email); // 인증 완료 상태도 제거
    }
}
