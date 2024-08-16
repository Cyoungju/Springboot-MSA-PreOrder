package com.example.ecommerceproject.user.service;

import com.example.ecommerceproject.user.dto.EmailDto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void createNum(String email);
    MimeMessage createMail(EmailDto emailDto);
    int sendMail(EmailDto emailDto);

    void mailCheck(String email, int certification);
}
