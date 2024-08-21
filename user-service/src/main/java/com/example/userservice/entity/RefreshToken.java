package com.example.userservice.entity;


import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 86400000L)
public class RefreshToken {
    @Id
    private String refreshToken;
    private String userName;
    private String role;

    public RefreshToken(String refreshToken, String userName, String role) {
        this.refreshToken = refreshToken;
        this.userName = userName;
        this.role = role;

    }
}
