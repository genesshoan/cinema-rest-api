package dev.genesshoan.cinema_rest_api.dto;

import java.time.LocalDate;

public record MovieResponseDTO(
    Long id,
    String title,
    Integer durationMinutes,
    String genre,
    LocalDate releaseDate,
    String description) {
}
