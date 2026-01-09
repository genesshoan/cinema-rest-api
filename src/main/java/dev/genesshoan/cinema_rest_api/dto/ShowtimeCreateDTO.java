package dev.genesshoan.cinema_rest_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used when creating a new showtime.
 *
 * Fields:
 * - startTime: start date and time for the showtime (required)
 * - endTime: end date and time for the showtime (required)
 * - basePrice: base ticket price for the showtime (required, non-negative)
 * - roomId: identifier of the room where the showtime will run (required)
 * - movieId: identifier of the movie to be shown (required)
 *
 * Validation annotations are applied to enforce presence and basic numeric
 * constraints. Business validation (e.g. start before end, overlapping
 * showtimes) is performed in the service layer.
 */
public record ShowtimeCreateDTO(
    @NotNull(message = "{showtime.start-time.required}") LocalDateTime startTime,
    @NotNull(message = "{showtime.end-time.required}") LocalDateTime endTime,
    @NotNull(message = "{showtime.base-price.required}") @Min(value = 0, message = "{showtime.base-price.min}") BigDecimal basePrice,
    @NotNull(message = "{showtime.room.required}") @Min(value = 1, message = "{id.min}") Long roomId,
    @NotNull(message = "{showtime.room.required}") @Min(value = 1, message = "{id.min}") Long movieId) {
}
