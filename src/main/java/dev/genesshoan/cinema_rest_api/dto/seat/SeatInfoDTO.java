package dev.genesshoan.cinema_rest_api.dto.seat;

import dev.genesshoan.cinema_rest_api.entity.SeatStatus;

/**
 * DTO representing basic information about a single seat.
 * 
 * <p>Used within {@link SeatMapRowDTO} to provide seat details
 * including position and current status.</p>
 * 
 * @param rowNumber The row number where the seat is located
 * @param seatNumber The seat number within the row
 * @param status The current availability status of the seat
 * 
 * @see SeatMapRowDTO
 * @see SeatStatus
 */
public record SeatInfoDTO(
    int rowNumber,
    int seatNumber,
    SeatStatus status) {
}
