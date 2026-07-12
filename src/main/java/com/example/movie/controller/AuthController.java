package com.example.movie.controller;

import com.example.movie.common.ApiResponse;
import com.example.movie.dto.request.UserRequestDto;
import com.example.movie.dto.response.LoginResponseDto;
import com.example.movie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid UserRequestDto request) {
        userService.register(request);
        return ApiResponse.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> login(@RequestBody @Valid UserRequestDto request) {
        return ApiResponse.ok(userService.login(request));
    }
}