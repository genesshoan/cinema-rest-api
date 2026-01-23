package dev.genesshoan.cinema_rest_api.dto.showtime;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO used to update scheduling and pricing information for an existing
 * showtime.
 *
 * Fields:
 * - startTime: updated start date/time (required)
 * - endTime: updated end date/time (required)
 * - basePrice: updated base ticket price (required, non-negative)
 *
 * Business validation (e.g. start before end) is enforced by the service
 * layer.
 */
public record ShowtimeUpdateDTO(
    @NotNull(message = "{showtime.start-time.required}") LocalDateTime startTime,
    @NotNull(message = "{showtime.end-time.required}") LocalDateTime endTime,
    @NotNull(message = "{showtime.base-price.required}") @Min(value = 0, message = "{showtime.base-price.min}") BigDecimal basePrice) {
}
