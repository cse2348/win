package com.example.win.service;

import com.example.win.dto.LoginRequestDto;
import com.example.win.dto.LoginResponseDto;
import com.example.win.entity.User;
import com.example.win.jwt.JwtUtil;
import com.example.win.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;  // JwtUtil 주입

    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 1. 사용자 조회
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 사용자가 없습니다."));

        // 2. 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String token = jwtUtil.createToken(user.getPhoneNumber());

        // 4. 로그인 응답 반환
        return LoginResponseDto.builder()
                .message("로그인 성공")
                .userId(user.getId())
                .token(token)  // 토큰 포함
                .build();
    }
}