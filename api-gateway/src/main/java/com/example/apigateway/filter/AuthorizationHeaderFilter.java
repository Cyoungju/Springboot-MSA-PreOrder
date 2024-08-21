package com.example.apigateway.filter;

import com.example.apigateway.redis.RedisBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    private final SecretKey secretKey;
    
    private final RedisBlacklistService blacklistService;

    public AuthorizationHeaderFilter(@Value("${jwt.secret.key}") String secret, RedisBlacklistService blacklistService) {
        super(Config.class);
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.blacklistService = blacklistService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Authorization 헤더가 있는지 확인
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                return onError(exchange,  "Authorization header가 없습니다.", HttpStatus.UNAUTHORIZED);
            }

            // Authorization 헤더에서 JWT를 추출
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer", "").trim();


            // JWT를 검증하고 클레임을 추출
            Claims claims;
            try {
                claims = validateToken(jwt);
            } catch (ExpiredJwtException e) {
                log.error("JWT 토큰이 만료되었습니다.", e);
                return onError(exchange, "Access token이 만료되었습니다.", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                // JWT 검증 중 오류가 발생한 경우 처리합니다.
                log.error("JWT 토큰 검증 오류", e);
                return onError(exchange, "JWT 토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED);
            }

            // 블랙리스트 확인
            if (blacklistService.isTokenBlacklisted(jwt)) {
                return onError(exchange, "The token is blacklisted", HttpStatus.UNAUTHORIZED);
            }

            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = claims.get("category", String.class);
            log.info(category +": category");

            if (!"access".equals(category)) {
                return onError(exchange, "유효하지 않은 access token입니다.", HttpStatus.UNAUTHORIZED);
            }

            // 클레임에서 사용자 이름과 역할을 추출
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            // 사용자 정보를 로그로 기록
            log.info("인증된 사용자: {}", username);
            log.info("사용자 역할: {}", role);

            // 토큰이 유효하면 요청을 계속 처리합니다.
            return chain.filter(exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-Authenticated-User", username)
                            .build()
                    ).build()
            );
        });
    }

    private Claims validateToken(String jwt) {
        // 로그로 JWT와 관련된 정보를 찍어봅니다.
        log.info("요청 JWT: {}", jwt);

        // JWT를 파싱하고 검증합니다.
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();

    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    public static class Config {

    }
}
