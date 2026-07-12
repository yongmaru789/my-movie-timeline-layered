package com.example.movie.controller;

import com.example.movie.dto.request.UserRequestDto;
import com.example.movie.dto.response.LoginResponseDto;
import com.example.movie.exception.DuplicateUsernameException;
import com.example.movie.exception.InvalidCredentialsException;
import com.example.movie.jwt.JwtUtil;
import com.example.movie.security.SecurityConfig;
import com.example.movie.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("중복된 아이디로 회원가입 시 409 응답")
    void register_duplicateUsername_returns409() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("password");

        doThrow(new DuplicateUsernameException("이미 존재하는 아이디입니다."))
                .when(userService).register(any(UserRequestDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("이미 존재하는 아이디입니다."));
    }

    @Test
    @DisplayName("로그인 성공 시 토큰과 사용자 정보를 반환한다")
    void login_success_returnsTokenAndUserInfo() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("password");

        LoginResponseDto loginResponse = new LoginResponseDto();
        loginResponse.setToken("test-jwt-token");
        loginResponse.setUserId(1L);
        loginResponse.setUsername("testuser");

        given(userService.login(any(UserRequestDto.class))).willReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시 401 응답")
    void login_userNotFound_returns401() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("nouser");
        request.setPassword("password");

        given(userService.login(any(UserRequestDto.class)))
                .willThrow(new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401 응답")
    void login_wrongPassword_returns401() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("wrong-password");

        given(userService.login(any(UserRequestDto.class)))
                .willThrow(new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("아이디가 없을 때와 비밀번호가 틀렸을 때 응답 메시지가 동일하다 (계정 존재 여부 노출 방지)")
    void login_failureMessagesAreIndistinguishable() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("someone");
        request.setPassword("password");

        given(userService.login(any(UserRequestDto.class)))
                .willThrow(new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."))
                .willThrow(new InvalidCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다."));

        String userNotFoundBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        String wrongPasswordBody = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertThat(userNotFoundBody).isEqualTo(wrongPasswordBody);
    }

    @Test
    @DisplayName("아이디를 비워서 회원가입하면 400 응답")
    void register_blankUsername_returns400() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("비밀번호가 너무 짧으면 400 응답")
    void register_shortPassword_returns400() throws Exception {
        UserRequestDto request = new UserRequestDto();
        request.setUsername("testuser");
        request.setPassword("123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
