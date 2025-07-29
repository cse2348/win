package com.example.win.controller;

import com.example.win.dto.LoginRequestDto;
import com.example.win.dto.LoginResponseDto;
import com.example.win.dto.SignupRequestDto;
import com.example.win.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequestDto requestDto) {
        userService.signup(requestDto);
        return "회원가입 성공";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}