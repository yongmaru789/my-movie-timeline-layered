package com.example.movie.dto.response;

import com.example.movie.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponseDto {

    private String token;
    private Long userId;
    private String username;

    public static LoginResponseDto of(String token, User user) {
        LoginResponseDto dto = new LoginResponseDto();
        dto.setToken(token);
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
