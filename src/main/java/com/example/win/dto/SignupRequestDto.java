package com.example.win.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequestDto {
    private String name;
    private String phoneNumber;
    private String password;
    private String region;
    private int age;
    private String guardianPhoneNumber;
}