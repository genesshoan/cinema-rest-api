package dev.genesshoan.cinema_rest_api.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomRequestDTO(
    @NotBlank(message = "{room.name.required}") @Size(min = 1, max = 255, message = "{room.name.size}") @Column(nullable = false, length = 255) String name,

    @NotNull(message = "{room.rows.required}") @Min(value = 1, message = "{room.rows.min}") @Column(nullable = false) Integer rows,

    @NotNull(message = "{room.seats.required}") @Min(value = 1, message = "{room.seats.min}") @Column(name = "seats_per_row", nullable = false) Integer seatsPerRow) {
}
