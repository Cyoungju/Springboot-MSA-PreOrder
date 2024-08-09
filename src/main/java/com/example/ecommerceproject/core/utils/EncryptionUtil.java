package com.example.ecommerceproject.core.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {
    @Value("${encry.secret.key}")
    private String base64EncodedKey;

    @Value("${encry.iv.key}")
    private String base64EncodedIv;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private SecretKey secretKey;
    private IvParameterSpec iv;

    // 생성자
    @PostConstruct
    public void init() {
        this.secretKey = getSecretKey(base64EncodedKey);
        this.iv = getIv(base64EncodedIv);
    }

    // Base64로 인코딩된 비밀 키를 반환하는 메서드
    private SecretKey getSecretKey(String base64EncodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // Base64로 인코딩된 IV를 반환하는 메서드
    private IvParameterSpec getIv(String base64EncodedIv) {
        byte[] decodedIv = Base64.getDecoder().decode(base64EncodedIv);
        return new IvParameterSpec(decodedIv);
    }

    // 데이터를 AES 알고리즘을 사용하여 암호화하는 메서드
    public String encrypt(String data) {
        byte[] encryptedBytes;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 암호화된 데이터를 AES 알고리즘을 사용하여 복호화하는 메서드
    public String decrypt(String encryptedData) {
        byte[] originalBytes;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            originalBytes = cipher.doFinal(decodedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
        return new String(originalBytes, StandardCharsets.UTF_8);
    }


}
