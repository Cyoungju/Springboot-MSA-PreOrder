package com.example.productservice.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + "localhost:6379");
        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());  // 키에 대한 직렬화기를 StringRedisSerializer로 변경
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());  // 값을 JSON 형식으로 직렬화
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
