package com.example.movie.dto.response;

import com.example.movie.entity.Movie;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieResponseDto {

    private Long id;
    private String title;
    private double rating;
    private String date;
    private String poster;
    private String userId;
    private Long tmdbId;
    private List<String> genres;

    public static MovieResponseDto from(Movie movie) {
        MovieResponseDto dto = new MovieResponseDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setRating(movie.getRating());
        dto.setDate(movie.getDate());
        dto.setPoster(movie.getPoster());
        dto.setUserId(movie.getUserId());
        dto.setTmdbId(movie.getTmdbId());
        dto.setGenres(movie.getGenres());
        return dto;
    }
}