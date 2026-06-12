package com.example.movie.service;

import com.example.movie.dto.request.UserRequestDto;
import com.example.movie.dto.response.UserResponseDto;

public interface UserService {

    void register(UserRequestDto request);

    String login(UserRequestDto request);

    UserResponseDto findByUsername(String username);
}