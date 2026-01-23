package dev.genesshoan.cinema_rest_api.dto.movie;

import java.time.LocalDate;

/**
 * Data Transfer Object for movie responses.
 * 
 * This immutable record encapsulates all sendable fields of a movie.
 * 
 * @param id              the movie id
 * @param title           the movie title
 * @param durationMinutes the movie duration in minutes
 * @param genre           the movie genre (e.g., "Action", "Drama")
 * @param releaseDate     the movie release date
 * @param description     optional detailed description of the movie
 *
 * @see MovieRequestDTO
 * @since 1.0.0
 */
public record MovieResponseDTO(
    Long id,
    String title,
    Integer durationMinutes,
    String genre,
    LocalDate releaseDate,
    String description) {
}
