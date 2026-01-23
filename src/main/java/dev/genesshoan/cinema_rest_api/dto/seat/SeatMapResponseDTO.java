package dev.genesshoan.cinema_rest_api.dto.seat;

import java.util.List;

/**
 * DTO representing the complete seat map for a showtime.
 * 
 * <p>Provides a comprehensive view of all seats organized by rows,
 * along with the current occupancy percentage. This is used to display
 * seat availability for customers selecting seats.</p>
 * 
 * @param showtimeId The ID of the showtime
 * @param rows List of rows, each containing seat information
 * @param occupancyPercentage Percentage of sold seats (0.0 to 100.0)
 * 
 * @see SeatMapRowDTO
 */
public record SeatMapResponseDTO(
    Long showtimeId,
    List<SeatMapRowDTO> rows,
    double occupancyPercentage) {
}
