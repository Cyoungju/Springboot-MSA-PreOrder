package com.example.userservice.service;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
@RequiredArgsConstructor
public class RedisBlacklistService {

    private final RedisCommands<String, String> redisCommands;


    // 블랙리스트에 토큰 추가
    public void addTokenToBlacklist(String token, long expirationTime) {
        // Redis에 블랙리스트 토큰을 저장하며, 만료 시간을 설정합니다.
        long expirationTimeSeconds = expirationTime / 1000;

        redisCommands.setex(token, expirationTimeSeconds, "blacklisted");

    }

    // 블랙리스트에서 토큰 확인
    public boolean isTokenBlacklisted(String token) {
        // Redis에서 해당 토큰이 존재하는지 확인합니다.
        return redisCommands.get(token) != null;
    }
}
