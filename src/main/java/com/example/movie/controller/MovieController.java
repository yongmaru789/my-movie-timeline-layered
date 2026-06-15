package com.example.movie.controller;

import com.example.movie.common.ApiResponse;
import com.example.movie.dto.request.MovieRequestDto;
import com.example.movie.dto.response.MovieResponseDto;
import com.example.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ApiResponse<Page<MovieResponseDto>> getMoviesByUserId(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equals("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.ok(movieService.getMoviesByUserId(userId, pageable));
    }

    @PostMapping
    public ApiResponse<MovieResponseDto> addMovie(@RequestBody MovieRequestDto request) {
        return ApiResponse.ok(movieService.addMovie(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponseDto> updateMovie(
            @PathVariable Long id,
            @RequestBody MovieRequestDto request) {
        return ApiResponse.ok(movieService.updateMovie(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ApiResponse.ok(null);
    }
}