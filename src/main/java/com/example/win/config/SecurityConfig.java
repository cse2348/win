package com.example.win.config;

import com.example.win.jwt.JwtAuthenticationFilter;
import com.example.win.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 보안 설정 클래스
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JwtAuthenticationFilter를 빈으로 등록
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil) {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    // 비밀번호 암호화에 사용할 BCryptPasswordEncoder를 빈으로 등록
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Spring Security의 보안 설정을 정의
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (API 서버 등에서 주로 사용)
                .authorizeHttpRequests(auth -> auth
                        // 로그인, 회원가입, H2 콘솔 경로는 인증 없이 허용
                        .requestMatchers("/login", "/signup", "/h2-console/**").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login")); // 로그아웃 후 이동할 경로 설정

        http.headers(headers -> headers.frameOptions(frame -> frame.disable())); // H2 콘솔 사용을 위한 헤더 설정
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT 필터를 인증 필터 전에 등록

        return http.build(); // 설정을 기반으로 SecurityFilterChain 객체 생성
    }
}