package com.example.movie.service.impl;

import com.example.movie.dto.request.MovieRequestDto;
import com.example.movie.dto.response.MovieResponseDto;
import com.example.movie.entity.Movie;
import com.example.movie.exception.MovieNotFoundException;
import com.example.movie.repository.MovieRepository;
import com.example.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public Page<MovieResponseDto> getMoviesByUserId(String userId, Pageable pageable) {
        return movieRepository.findByUserId(userId, pageable)
                .map(MovieResponseDto::from);
    }

    @Override
    public MovieResponseDto addMovie(MovieRequestDto request, String userId) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setRating(request.getRating());
        movie.setDate(request.getDate());
        movie.setPoster(request.getPoster());
        movie.setUserId(userId);
        movie.setTmdbId(request.getTmdbId());
        movie.setGenres(request.getGenres());
        return MovieResponseDto.from(movieRepository.save(movie));
    }

    @Override
    public MovieResponseDto updateMovie(Long id, MovieRequestDto request, String userId) {
        Movie movie = movieRepository.findById(id)
                .filter(m -> m.getUserId().equals(userId))
                .orElseThrow(() -> new MovieNotFoundException(id));
        movie.setTitle(request.getTitle());
        movie.setRating(request.getRating());
        movie.setDate(request.getDate());
        movie.setPoster(request.getPoster());
        movie.setGenres(request.getGenres());
        return MovieResponseDto.from(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(Long id, String userId) {
        Movie movie = movieRepository.findById(id)
                .filter(m -> m.getUserId().equals(userId))
                .orElseThrow(() -> new MovieNotFoundException(id));
        movieRepository.delete(movie);
    }
}