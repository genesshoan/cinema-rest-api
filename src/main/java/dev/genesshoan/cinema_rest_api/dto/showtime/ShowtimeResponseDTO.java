package dev.genesshoan.cinema_rest_api.dto.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;

/**
 * DTO returned to clients representing a showtime.
 *
 * Fields:
 * - id: identifier of the showtime
 * - startTime / endTime: scheduling window for the showtime
 * - basePrice: base ticket price
 * - status: current lifecycle state
 * - roomName / movieTitle: denormalized display fields for convenience
 */
public record ShowtimeResponseDTO(
    Long id,
    LocalDateTime startTime,
    LocalDateTime endTime,
    BigDecimal basePrice,
    ShowtimeStatus status,
    String roomName,
    String movieTitle) {
}
