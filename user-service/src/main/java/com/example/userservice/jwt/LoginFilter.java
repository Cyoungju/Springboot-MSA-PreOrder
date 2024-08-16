package com.example.userservice.jwt;

import com.example.userservice.core.exception.CustomException;
import com.example.userservice.entity.Refresh;
import com.example.userservice.repository.RefreshRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

// formLogin disable 했기 때문에 UsernamePasswordAuthenticationFilter가 작동 하지 않음 -> 커스텀 필터 작성
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter  {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final RefreshRepository refreshRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // JSON 형식의 요청 본문을 읽습니다.
        try {
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), new TypeReference<Map<String, String>>() {});

            //클라이언트 요청에서 username, password 추출
            String email = credentials.get("email");
            String password = credentials.get("password");

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new CustomException("로그인 실패!");
        }
    }

    //로그인 성공시 실행하는 메소드 (JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        // 유저정보
        String username = authentication.getName();

        // 유저의 Role값
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        addRefreshEntity(username,refresh,86400000L);

        //응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());


        // JSON 형식의 응답을 작성합니다.
        response.setContentType("application/json; charset=UTF-8"); // UTF-8 인코딩 설정
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();
        writer.write("{\"message\":\"로그인 성공!\", \"AccessToken\":\"" + access + "\", \"RefreshToken\" : \"" + refresh+ "\"}");
        writer.flush();
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        // JSON 형식의 에러 메시지를 작성합니다.
        response.setContentType("application/json; charset=UTF-8"); // UTF-8 인코딩 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

        PrintWriter writer = response.getWriter();
        writer.write("{\"error\":\"아이디나 비밀번호가 틀렸습니다\"}");
        writer.flush();
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60); //생명주기
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true); //XSS 공격방어

        return cookie;
    }
}
