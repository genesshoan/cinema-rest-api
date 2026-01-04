package dev.genesshoan.cinema_rest_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record MovieRequestDTO(
    @NotBlank(message = "{movie.title.required}") @Size(min = 1, max = 255, message = "{movie.title.size}") String title,

    @NotNull(message = "{movie.durationMinutes.required}") @Min(value = 1, message = "{movie.durationMinutes.min}") Integer durationMinutes,

    @NotBlank(message = "{movie.genre.required}") @Size(max = 30, message = "movie.genre.size") String genre,

    @NotNull(message = "{movie.releaseDate.required}") @PastOrPresent(message = "{movie.releaseDate.past}") LocalDate releaseDate,

    @Size(max = 5000, message = "{movie.description.size}") String description) {
}
