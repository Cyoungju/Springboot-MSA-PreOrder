package com.example.userservice.config;


import com.example.userservice.jwt.JwtFilter;
import com.example.userservice.jwt.JwtUtil;
import com.example.userservice.jwt.LoginFilter;
import com.example.userservice.repository.RefreshRepository;
import com.example.userservice.jwt.CustomLogoutFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtUtil jwtUtil;

    private final ObjectMapper objectMapper;

    private final RefreshRepository refreshRepository;


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //csrf disable
        http
                .csrf((auth) -> auth.disable());
        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/member/login","/api/member/sign-up", "/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // product get 요청만 접근허용
                        .requestMatchers(HttpMethod.GET, "/api/products").permitAll() // product get 요청만 접근허용
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        // 로그인 필터 설정
        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, objectMapper, refreshRepository);
        loginFilter.setFilterProcessesUrl("/api/member/login"); // 로그인 경로 설정

        // 로그아웃 필터설정
        CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(jwtUtil,refreshRepository);


//        http
//                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        // 로그아웃 커스텀 필터 등록
        http
                .addFilterBefore(customLogoutFilter, LoginFilter.class);
        
        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}