//package com.example.ecommerceproject.member.util;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class EncryptionUtilTest {
//
//    private EncryptionUtil encryptionUtil;
//
//    @BeforeEach
//    void setUp() {
//        // Base64 인코딩된 키와 IV 값을 사용하여 EncryptionUtil 인스턴스 생성
//        encryptionUtil = new EncryptionUtil();
//    }
//
//
//    @Test
//    void testEncryptionAndDecryption() {
//        // 원본 데이터
//        String originalData = "Test@email.com";
//
//        // 데이터 암호화
//        String encryptedData = encryptionUtil.encrypt(originalData);
//        assertNotNull(encryptedData, "암호화된 데이터는 null이 아니어야 합니다.");
//        System.out.println("암호화된 데이터: " + encryptedData);
//
//        // 데이터 복호화
//        String decryptedData = encryptionUtil.decrypt(encryptedData);
//        assertNotNull(decryptedData, "복호화된 데이터는 null이 아니어야 합니다.");
//        System.out.println("복호화된 데이터: " + decryptedData);
//
//        // 원본 데이터와 복호화된 데이터가 동일한지 확인
//        assertEquals(originalData, decryptedData, "복호화된 데이터는 원본 데이터와 동일해야 합니다.");
//
//    }
//}