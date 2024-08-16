package com.example.userservice.controller;

import com.example.userservice.core.utils.ApiUtils;
import com.example.userservice.dto.EmailDto;
import com.example.userservice.dto.VerificationCodeStore;
import com.example.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mail")
public class EmailController {
    private final EmailService emailService;
    private final VerificationCodeStore verificationCodeStore;

    // 이메일 전송
    @PostMapping("/send")
    public ResponseEntity<?> mailSend(@RequestBody EmailDto emailDto) {
        int num = emailService.sendMail(emailDto);
        return ResponseEntity.ok( ApiUtils.success(num) );
    }

    // 이메일 체크
    @PostMapping("/check")
    public ResponseEntity<?> mailCheck(@RequestBody EmailDto emailDto) {
        String email = emailDto.getEmail();
        int certification = emailDto.getCertification();
        emailService.mailCheck(email, certification);
        System.out.println(verificationCodeStore.getVerificationStatus(email));
        return ResponseEntity.ok( ApiUtils.success("인증되었습니다.") );
    }


}
