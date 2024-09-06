package com.example.userservice.service;

import com.example.userservice.entity.RefreshToken;
import com.example.userservice.jwt.JwtUtil;
import com.example.userservice.repository.RefreshRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final RefreshRepository refreshRepository; // RefreshService 주입

    private final JwtUtil jwtUtil;


    @Override
    public ResponseEntity<?> reissue(HttpServletResponse response, HttpServletRequest request) throws IOException {
        // Cookie에서 refresh토큰 가져오기
        String refreshTokenString = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refreshTokenString = cookie.getValue();
                    break;
                }
            }
        }
        if (refreshTokenString == null) {
            //response status code
            return new ResponseEntity<>("refresh token 없습니다", HttpStatus.BAD_REQUEST);
        }

        // Redis에서 리프레쉬 토큰 조회
        RefreshToken redisGetRefreshToken = refreshRepository.findById(refreshTokenString)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String username = redisGetRefreshToken.getUserName();

        String role = redisGetRefreshToken.getRole();

        String access = jwtUtil.createJwt("access", username, redisGetRefreshToken.getRole() , 600000L);
        String newRefreshToken = UUID.randomUUID().toString();
        RefreshToken redis = new RefreshToken(newRefreshToken, username, role);
        refreshRepository.save(redis);

        // 기존 리프레쉬 토큰을 Redis에서 삭제
        refreshRepository.deleteById(refreshTokenString);

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(jwtUtil.createCookie("refresh", newRefreshToken));
        response.setStatus(HttpStatus.OK.value());

        // JSON 형식의 응답을 작성합니다.
        response.setContentType("application/json; charset=UTF-8"); // UTF-8 인코딩 설정
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();
        writer.write("{\"message\":\"AccessToken 재발급 완료!\", \"AccessToken\":\"" + access + "\", \"RefreshToken\" : \"" + newRefreshToken+ "\"}");
        writer.flush();

        return new ResponseEntity<>(writer, HttpStatus.OK);

    }


}
