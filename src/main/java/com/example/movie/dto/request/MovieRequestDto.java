package com.example.movie.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieRequestDto {

    private String title;
    private double rating;
    private String date;
    private String poster;
    private String userId;
    private Long tmdbId;
    private List<String> genres;
}