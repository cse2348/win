package com.example.win.service;

import com.example.win.dto.LoginRequestDto;
import com.example.win.dto.LoginResponseDto;
import com.example.win.dto.SignupRequestDto;
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

    // 회원가입
    public void signup(SignupRequestDto requestDto) {
        if (userRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }

        User user = User.builder()
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .password(passwordEncoder.encode(requestDto.getPassword())) // 암호화 저장
                .region(requestDto.getRegion())
                .age(requestDto.getAge())
                .guardianPhoneNumber(requestDto.getGuardianPhoneNumber())
                .build();

        userRepository.save(user);
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 존재 확인
        User user = userRepository.findByPhoneNumber(requestDto.getPhoneNumber())
                .orElseThrow(() -> new IllegalArgumentException("해당 전화번호로 가입된 사용자가 없습니다."));

        // 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtUtil.createToken(user.getPhoneNumber());

        // 응답 DTO 반환
        return LoginResponseDto.builder()
                .message("로그인 성공")
                .userId(user.getId())
                .token(token)
                .build();
    }
}