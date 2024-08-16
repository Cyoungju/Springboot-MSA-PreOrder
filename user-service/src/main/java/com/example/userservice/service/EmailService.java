package com.example.userservice.service;

import com.example.userservice.dto.EmailDto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void createNum(String email);
    MimeMessage createMail(EmailDto emailDto);
    int sendMail(EmailDto emailDto);

    void mailCheck(String email, int certification);
}
