package com.example.apigateway.redis;

import io.lettuce.core.api.sync.RedisCommands;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class RedisBlacklistService {

    private final RedisCommands<String, String> redisCommands;

    // 블랙리스트에서 토큰 확인
    public boolean isTokenBlacklisted(String token) {
        // Redis에서 해당 토큰이 존재하는지 확인합니다.
        return redisCommands.get(token) != null;
    }
}
