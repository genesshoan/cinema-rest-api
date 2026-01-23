package dev.genesshoan.cinema_rest_api.dto.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating or updating a cinema room.
 *
 * <p>
 * Carries the minimal information required to create or update a room:
 * <ul>
 *   <li>{@code name} - human-readable room identifier (required, 1-255 chars)</li>
 *   <li>{@code rows} - number of seat rows in the room (required, >= 1)</li>
 *   <li>{@code seatsPerRow} - number of seats per row (required, >= 1)</li>
 * </ul>
 * </p>
 *
 * Validation constraints are applied to ensure incoming requests are valid.
 */
public record RoomRequestDTO(
    @NotBlank(message = "{room.name.required}") @Size(min = 1, max = 255, message = "{room.name.size}") String name,

    @NotNull(message = "{room.rows.required}") @Min(value = 1, message = "{room.rows.min}") Integer rows,

    @NotNull(message = "{room.seats.required}") @Min(value = 1, message = "{room.seats.min}") Integer seatsPerRow) {
}
