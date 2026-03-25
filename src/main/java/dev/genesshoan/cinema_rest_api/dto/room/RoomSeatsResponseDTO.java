package dev.genesshoan.cinema_rest_api.dto.room;

/**
 * Response DTO for a room seat configuration summary.
 *
 * @param roomId        room identifier
 * @param roomName      room display name
 * @param rows          total rows configured in the room
 * @param seatsPerRow   seats configured per row
 * @param totalCapacity computed room capacity (rows * seatsPerRow)
 */
public record RoomSeatsResponseDTO(
    Long roomId,
    String roomName,
    Integer rows,
    Integer seatsPerRow,
    Integer totalCapacity) {
}
