package dev.genesshoan.cinema_rest_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomRequestDTO(
    @NotBlank(message = "{room.name.required}") @Size(min = 1, max = 255, message = "{room.name.size}") String name,

    @NotNull(message = "{room.rows.required}") @Min(value = 1, message = "{room.rows.min}") Integer rows,

    @NotNull(message = "{room.seats.required}") @Min(value = 1, message = "{room.seats.min}") Integer seatsPerRow) {
}
