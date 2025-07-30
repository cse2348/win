package com.example.win.controller;

import com.example.win.dto.LoginRequestDto;
import com.example.win.dto.LoginResponseDto;
import com.example.win.dto.SignupRequestDto;
import com.example.win.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto requestDto) {
        try {
            userService.signup(requestDto);
            return ResponseEntity.ok(Map.of("message", "회원가입 성공"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "회원가입 실패",
                    "message", e.getMessage()
            ));
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}