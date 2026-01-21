package dev.genesshoan.cinema_rest_api.dto;

import dev.genesshoan.cinema_rest_api.entity.SeatStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for seat creation and update requests.
 * 
 * This immutable record encapsulates all required fields
 * for creating or updating a seat in a showtime. All fields are validated using
 * Bean Validation annotations.
 * 
 * <p>
 * Validation rules:
 * <ul>
 * <li>Row number: required, minimum 1</li>
 * <li>Seat number: required, minimum 1</li>
 * <li>Status: required, must be a valid SeatStatus enum value</li>
 * <li>Showtime ID: required, minimum 1</li>
 * </ul>
 * </p>
 * 
 * @param rowNumber   the row number where the seat is located (1-indexed)
 * @param seatNumber  the seat number within the row (1-indexed)
 * @param status      the current status of the seat (AVAILABLE or SOLD)
 * @param showtimeId  the ID of the showtime this seat belongs to
 *
 * @see SeatResponseDTO
 * @since 1.0.0
 */
public record SeatRequestDTO(
    @NotNull(message = "{seat.rowNumber.required}") @Min(value = 1, message = "{seat.rowNumber.min}") Integer rowNumber,

    @NotNull(message = "{seat.seatNumber.required}") @Min(value = 1, message = "{seat.seatNumber.min}") Integer seatNumber,

    @NotNull(message = "{seat.status.required}") SeatStatus status,

    @NotNull(message = "{seat.showtimeId.required}") @Min(value = 1, message = "{seat.showtimeId.min}") Long showtimeId) {
}
