package com.example.movie.service;

import com.example.movie.dto.request.UserRequestDto;
import com.example.movie.dto.response.LoginResponseDto;
import com.example.movie.entity.User;
import com.example.movie.exception.DuplicateUsernameException;
import com.example.movie.exception.InvalidCredentialsException;
import com.example.movie.jwt.JwtUtil;
import com.example.movie.repository.UserRepository;
import com.example.movie.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("로그인 성공 시 회원 조회는 한 번만 발생한다 (중복 조회 없음)")
    void login_success_queriesUserOnlyOnce() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded-password");

        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("password");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password", "encoded-password")).willReturn(true);
        given(jwtUtil.generateToken("testuser")).willReturn("test-jwt-token");

        // When
        LoginResponseDto result = userService.login(request);

        // Then
        assertThat(result.getToken()).isEqualTo("test-jwt-token");
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하면 예외 발생")
    void login_userNotFound_throwsException() {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("nouser");
        request.setPassword("password");

        given(userRepository.findByUsername("nouser")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("비밀번호가 틀리면 예외 발생")
    void login_wrongPassword_throwsException() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded-password");

        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("wrong-password");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrong-password", "encoded-password")).willReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("중복된 아이디로 회원가입하면 예외 발생")
    void register_duplicateUsername_throwsException() {
        User existing = new User();
        existing.setUsername("testuser");

        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("password");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(DuplicateUsernameException.class);
    }
}
