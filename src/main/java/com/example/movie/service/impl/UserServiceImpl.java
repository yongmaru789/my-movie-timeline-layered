package com.example.movie.service.impl;

import com.example.movie.dto.request.UserRequestDto;
import com.example.movie.dto.response.UserResponseDto;
import com.example.movie.entity.User;
import com.example.movie.repository.UserRepository;
import com.example.movie.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(UserRequestDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public String login(UserRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }
        return jwtUtil.generateToken(request.getUsername());
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));
        return UserResponseDto.from(user);
    }
}