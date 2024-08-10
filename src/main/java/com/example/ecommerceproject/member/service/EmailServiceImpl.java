package com.example.ecommerceproject.member.service;


import com.example.ecommerceproject.core.exception.CustomException;
import com.example.ecommerceproject.member.dto.EmailDto;
import com.example.ecommerceproject.member.dto.VerificationCodeStore;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;
    private static final String senderEmail = "dudwn3528@gmail.com"; //이메일 발신자
    private static int number; // 인증번호 저장할 변수

    private final VerificationCodeStore verificationCodeStore;


    @Override
    public void createNum(String email) {
        // 인증번호 랜덤생성
        number = (int) (Math.random() * (90000)) + 100000;
        verificationCodeStore.storeCode(email, number);
    }

    @Override
    public MimeMessage createMail(EmailDto emailDto) {
        String email = emailDto.getEmail();
        createNum(email);
        MimeMessage message = javaMailSender.createMimeMessage();


        try {
            message.setFrom(senderEmail); // 발신자 이메일 주소
            message.setRecipients(MimeMessage.RecipientType.TO,email); // 수신자 이메일 주소
            message.setSubject("이메일 인증번호 확인"); // 이메일 제목

            //이메일 본문 작성
            String body ="";
            body+="<h3>"+"요청하신 인증번호 입니다!"+"</h3>";
            body+="<h1>"+  verificationCodeStore.getCode(email) +"</h1>";

            // 본문을 UTF-8 인코딩과 HTML 형식으로 설정
            message.setText(body, "UTF-8", "html");

        } catch (MessagingException e) {
            e.printStackTrace(); // 이메일 생성 과정에서 예외가 발생할 경우 스택 트레이스를 출력합니다.
        }

        return message;
    }

    
    // 이메일 전송 메소드
    @Override
    public int sendMail(EmailDto emailDto) {
        MimeMessage message = createMail(emailDto);
        javaMailSender.send(message);
        
        return verificationCodeStore.getCode(emailDto.getEmail());
    }

    @Override
    public void mailCheck(String email, int certification) {
        Integer storedCode = verificationCodeStore.getCode(email);

        if (storedCode == null || !storedCode.equals(certification)) {
            throw new CustomException("인증번호가 다릅니다.");
        }

        // 인증상태 변경
        verificationCodeStore.setVerificationStatus(email, true);

    }
}
