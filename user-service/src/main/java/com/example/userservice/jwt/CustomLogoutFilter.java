package com.example.userservice.jwt;

import com.example.userservice.repository.RefreshRepository;
import com.example.userservice.service.RedisBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final RefreshRepository refreshRepository;
    private final RedisBlacklistService blacklistService;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);

    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //path and method verify
        String requestUri = request.getRequestURI();
        if (!requestUri.equals("/api/member/logout")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        //get refresh token
        String refreshTokenString = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {
                refreshTokenString = cookie.getValue();
            }
        }

        //refresh null check
        if (refreshTokenString == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else {

            String authorizationHeader = request.getHeader("Authorization");
            String accessToken = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                accessToken = authorizationHeader.substring(7); // Remove "Bearer " prefix
            }

            // 토큰을 블랙리스트에 추가
            blacklistService.addTokenToBlacklist(accessToken, 600000L);

            // Redis에서 해당 리프레쉬 토큰 삭제
            refreshRepository.deleteById(refreshTokenString);

            //Refresh 토큰 Cookie 값 0
            Cookie cookie = new Cookie("refresh", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            response.setStatus(HttpServletResponse.SC_OK);

            // JSON 형식의 응답을 작성합니다.
            response.setContentType("application/json; charset=UTF-8"); // UTF-8 인코딩 설정
            response.setStatus(HttpServletResponse.SC_OK);

            PrintWriter writer = response.getWriter();
            writer.write("{\"message\":\"로그아웃 성공!\"}");
            writer.flush();
        }
    }
}
