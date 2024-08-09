package com.example.ecommerceproject.core.jwt;

import com.example.ecommerceproject.member.entity.CustomUserDetails;
import com.example.ecommerceproject.member.entity.Member;
import com.example.ecommerceproject.member.entity.MemberRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request 에서 Authorization 헤더 찾음
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        System.out.println("authorization now");
        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {
            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token); //ROLE_USER

        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5); // "ROLE_"의 길이인 5를 사용하여 접두사를 제거
        }
        MemberRole memberRole = MemberRole.USER;

        if (role != null) {
            switch (role) {
                case "USER":
                    memberRole = MemberRole.USER;
                case "ADMIN":
                    memberRole = MemberRole.ADMIN;
                case "MANAGER":
                    memberRole = MemberRole.MANAGER;
            }
        }

        //member 생성하여 값 set
        Member member = Member.builder()
                .email(username)
                .password("temppassword")
                .build();
        // 기본 사용자 - defalt
        member.addRole(memberRole);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
