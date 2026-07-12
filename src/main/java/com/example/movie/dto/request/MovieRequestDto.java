package com.example.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MovieRequestDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private double rating;

    @NotBlank(message = "날짜는 필수입니다.")
    private String date;

    private String poster;
    private String userId;
    private Long tmdbId;
    private List<String> genres;
}