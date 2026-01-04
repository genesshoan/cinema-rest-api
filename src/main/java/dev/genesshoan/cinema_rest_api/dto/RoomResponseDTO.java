package dev.genesshoan.cinema_rest_api.dto;

public record RoomResponseDTO(
    Long id,
    String name,
    Integer rows,
    Integer seatsPerRow) {
}
