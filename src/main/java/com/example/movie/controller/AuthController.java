package com.example.movie.controller;

import com.example.movie.common.ApiResponse;
import com.example.movie.dto.request.UserRequestDto;
import com.example.movie.dto.response.UserResponseDto;
import com.example.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody UserRequestDto request) {
        userService.register(request);
        return ApiResponse.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody UserRequestDto request) {
        String token = userService.login(request);
        UserResponseDto user = userService.findByUsername(request.getUsername());
        return ApiResponse.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername()
        ));
    }
}