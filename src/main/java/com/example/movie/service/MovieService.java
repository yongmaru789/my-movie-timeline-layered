package com.example.movie.service;

import com.example.movie.dto.request.MovieRequestDto;
import com.example.movie.dto.response.MovieResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieService {

    Page<MovieResponseDto> getMoviesByUserId(String userId, Pageable pageable);

    MovieResponseDto addMovie(MovieRequestDto request, String userId);

    MovieResponseDto updateMovie(Long id, MovieRequestDto request, String userId);

    void deleteMovie(Long id, String userId);
}