package com.example.movie.controller;

import com.example.movie.dto.request.MovieRequestDto;
import com.example.movie.dto.response.MovieResponseDto;
import com.example.movie.dto.response.UserResponseDto;
import com.example.movie.jwt.JwtUtil;
import com.example.movie.security.SecurityConfig;
import com.example.movie.service.MovieService;
import com.example.movie.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@Import(SecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserService userService;

    private String token;
    private MovieResponseDto movieResponseDto;

    @BeforeEach
    void setUp() {
        token = "test-token";

        given(jwtUtil.validateToken(token)).willReturn(true);
        given(jwtUtil.extractUsername(token)).willReturn("testuser");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("testuser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setUsername("testuser");
        given(userService.findByUsername("testuser")).willReturn(userResponseDto);

        movieResponseDto = new MovieResponseDto();
        movieResponseDto.setId(1L);
        movieResponseDto.setTitle("인터스텔라");
        movieResponseDto.setRating(4.5);
        movieResponseDto.setDate("2024-01-01");
        movieResponseDto.setUserId("1");
        movieResponseDto.setGenres(List.of("SF", "드라마"));
    }

    @Test
    @DisplayName("영화 등록 API 성공")
    void addMovie_success() throws Exception {
        // Given
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("인터스텔라");
        request.setRating(4.5);
        request.setDate("2024-01-01");
        request.setGenres(List.of("SF", "드라마"));

        given(movieService.addMovie(any(MovieRequestDto.class), eq("1")))
                .willReturn(movieResponseDto);

        // When & Then
        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("인터스텔라"))
                .andExpect(jsonPath("$.data.rating").value(4.5));
    }

    @Test
    @DisplayName("제목 없이 영화를 등록하면 400 응답")
    void addMovie_blankTitle_returns400() throws Exception {
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("");
        request.setRating(4.5);
        request.setDate("2024-01-01");

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("영화 목록 조회 API 성공")
    void getMovies_success() throws Exception {
        // Given
        Page<MovieResponseDto> page = new PageImpl<>(List.of(movieResponseDto));
        given(movieService.getMoviesByUserId(eq("1"), any(Pageable.class)))
                .willReturn(page);

        // When & Then
        mockMvc.perform(get("/api/movies")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("인터스텔라"));
    }

    @Test
    @DisplayName("size가 상한(100)을 넘으면 최대값으로 제한된다")
    void getMovies_sizeExceedsLimit_isClampedToMax() throws Exception {
        // Given
        Page<MovieResponseDto> page = new PageImpl<>(List.of(movieResponseDto));
        given(movieService.getMoviesByUserId(eq("1"), any(Pageable.class)))
                .willReturn(page);

        // When
        mockMvc.perform(get("/api/movies")
                        .header("Authorization", "Bearer " + token)
                        .param("size", "9999"))
                .andExpect(status().isOk());

        // Then
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(movieService).getMoviesByUserId(eq("1"), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(100);
    }

    @Test
    @DisplayName("인증 토큰 없이 요청하면 JSON 형식의 401 응답을 받는다")
    void getMovies_withoutToken_returnsJsonUnauthorized() throws Exception {
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("토큰이 없습니다."));
    }

    @Test
    @DisplayName("CORS preflight(OPTIONS) 요청은 인증 없이도 허용된다")
    void preflightRequest_isAllowedWithoutAuthentication() throws Exception {
        mockMvc.perform(options("/api/movies")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    @DisplayName("영화 수정 API 성공")
    void updateMovie_success() throws Exception {
        // Given
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("인터스텔라 수정");
        request.setRating(5.0);
        request.setDate("2024-01-01");

        MovieResponseDto updatedResponse = new MovieResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("인터스텔라 수정");
        updatedResponse.setRating(5.0);

        given(movieService.updateMovie(eq(1L), any(MovieRequestDto.class), eq("1")))
                .willReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/movies/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("인터스텔라 수정"))
                .andExpect(jsonPath("$.data.rating").value(5.0));
    }

    @Test
    @DisplayName("영화 삭제 API 성공")
    void deleteMovie_success() throws Exception {
        // Given
        doNothing().when(movieService).deleteMovie(1L, "1");

        // When & Then
        mockMvc.perform(delete("/api/movies/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}