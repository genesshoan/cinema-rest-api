package dev.genesshoan.cinema_rest_api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for movie creation and update requests.
 * 
 * This immutable record encapsulates all required and optional fields
 * for creating or updating a movie. All fields are validated using
 * Bean Validation annotations.
 * 
 * <p>
 * Validation rules:
 * <ul>
 * <li>Title: required, 1-255 characters</li>
 * <li>Duration: required, minimum 1 minute</li>
 * <li>Genre: required, maximum 30 characters</li>
 * <li>Release date: required, must be in the past or present</li>
 * <li>Description: optional, maximum 5000 characters</li>
 * </ul>
 * </p>
 * 
 * @param title           the movie title
 * @param durationMinutes the movie duration in minutes
 * @param genre           the movie genre (e.g., "Action", "Drama")
 * @param releaseDate     the movie release date
 * @param description     optional detailed description of the movie
 *
 * @see MovieResponseDTO
 * @since 1.0.0
 */
public record MovieRequestDTO(
    @NotBlank(message = "{movie.title.required}") @Size(min = 1, max = 255, message = "{movie.title.size}") String title,

    @NotNull(message = "{movie.durationMinutes.required}") @Min(value = 1, message = "{movie.durationMinutes.min}") Integer durationMinutes,

    @NotBlank(message = "{movie.genre.required}") @Size(max = 30, message = "{movie.genre.size}") String genre,

    @NotNull(message = "{movie.releaseDate.required}") @PastOrPresent(message = "{movie.releaseDate.past}") LocalDate releaseDate,

    @Size(max = 5000, message = "{movie.description.size}") String description) {
}
