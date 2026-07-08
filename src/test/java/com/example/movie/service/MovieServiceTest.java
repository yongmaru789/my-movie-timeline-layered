package com.example.movie.service;

import com.example.movie.dto.request.MovieRequestDto;
import com.example.movie.dto.response.MovieResponseDto;
import com.example.movie.entity.Movie;
import com.example.movie.exception.MovieNotFoundException;
import com.example.movie.repository.MovieRepository;
import com.example.movie.service.impl.MovieServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    @DisplayName("영화 등록 성공")
    void addMovie_success() {
        // Given
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("인터스텔라");
        request.setRating(4.5);
        request.setDate("2024-01-01");
        request.setGenres(List.of("SF", "드라마"));

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("인터스텔라");
        savedMovie.setRating(4.5);
        savedMovie.setDate("2024-01-01");
        savedMovie.setUserId("1");
        savedMovie.setGenres(List.of("SF", "드라마"));

        given(movieRepository.save(any(Movie.class))).willReturn(savedMovie);

        // When
        MovieResponseDto result = movieService.addMovie(request, "1");

        // Then
        assertThat(result.getTitle()).isEqualTo("인터스텔라");
        assertThat(result.getRating()).isEqualTo(4.5);
        assertThat(result.getUserId()).isEqualTo("1");
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("영화 수정 성공")
    void updateMovie_success() {
        // Given
        Movie existingMovie = new Movie();
        existingMovie.setId(1L);
        existingMovie.setTitle("인터스텔라");
        existingMovie.setRating(4.5);
        existingMovie.setUserId("1");

        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("인터스텔라 수정");
        request.setRating(5.0);

        Movie updatedMovie = new Movie();
        updatedMovie.setId(1L);
        updatedMovie.setTitle("인터스텔라 수정");
        updatedMovie.setRating(5.0);

        given(movieRepository.findById(1L)).willReturn(Optional.of(existingMovie));
        given(movieRepository.save(any(Movie.class))).willReturn(updatedMovie);

        // When
        MovieResponseDto result = movieService.updateMovie(1L, request, "1");

        // Then
        assertThat(result.getTitle()).isEqualTo("인터스텔라 수정");
        assertThat(result.getRating()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("존재하지 않는 영화 수정 시 예외 발생")
    void updateMovie_notFound() {
        // Given
        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("인터스텔라");

        given(movieRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.updateMovie(999L, request, "1"))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("다른 사용자의 영화 수정 시도 시 예외 발생")
    void updateMovie_notOwner() {
        // Given
        Movie existingMovie = new Movie();
        existingMovie.setId(1L);
        existingMovie.setUserId("1");

        MovieRequestDto request = new MovieRequestDto();
        request.setTitle("인터스텔라 수정");

        given(movieRepository.findById(1L)).willReturn(Optional.of(existingMovie));

        // When & Then
        assertThatThrownBy(() -> movieService.updateMovie(1L, request, "2"))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    @DisplayName("영화 삭제 성공")
    void deleteMovie_success() {
        // Given
        Movie existingMovie = new Movie();
        existingMovie.setId(1L);
        existingMovie.setUserId("1");

        given(movieRepository.findById(1L)).willReturn(Optional.of(existingMovie));

        // When
        movieService.deleteMovie(1L, "1");

        // Then
        verify(movieRepository, times(1)).delete(existingMovie);
    }

    @Test
    @DisplayName("존재하지 않는 영화 삭제 시 예외 발생")
    void deleteMovie_notFound() {
        // Given
        given(movieRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.deleteMovie(999L, "1"))
                .isInstanceOf(MovieNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("다른 사용자의 영화 삭제 시도 시 예외 발생")
    void deleteMovie_notOwner() {
        // Given
        Movie existingMovie = new Movie();
        existingMovie.setId(1L);
        existingMovie.setUserId("1");

        given(movieRepository.findById(1L)).willReturn(Optional.of(existingMovie));

        // When & Then
        assertThatThrownBy(() -> movieService.deleteMovie(1L, "2"))
                .isInstanceOf(MovieNotFoundException.class);
    }
}