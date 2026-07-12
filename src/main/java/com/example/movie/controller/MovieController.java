package com.example.movie.controller;

import com.example.movie.common.ApiResponse;
import com.example.movie.dto.request.MovieRequestDto;
import com.example.movie.dto.response.MovieResponseDto;
import com.example.movie.dto.response.UserResponseDto;
import com.example.movie.service.MovieService;
import com.example.movie.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private static final int MAX_PAGE_SIZE = 100;

    private final MovieService movieService;
    private final UserService userService;

    @GetMapping
    public ApiResponse<Page<MovieResponseDto>> getMoviesByUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equals("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), sort);
        return ApiResponse.ok(movieService.getMoviesByUserId(resolveUserId(), pageable));
    }

    @PostMapping
    public ApiResponse<MovieResponseDto> addMovie(@RequestBody @Valid MovieRequestDto request) {
        return ApiResponse.ok(movieService.addMovie(request, resolveUserId()));
    }

    @PutMapping("/{id}")
    public ApiResponse<MovieResponseDto> updateMovie(
            @PathVariable Long id,
            @RequestBody @Valid MovieRequestDto request) {
        return ApiResponse.ok(movieService.updateMovie(id, request, resolveUserId()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id, resolveUserId());
        return ApiResponse.ok(null);
    }

    private String resolveUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserResponseDto user = userService.findByUsername(username);
        return String.valueOf(user.getId());
    }
}