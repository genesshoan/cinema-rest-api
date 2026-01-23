package dev.genesshoan.cinema_rest_api.dto;

import java.util.List;

/**
 * DTO representing a single row in a seat map.
 * 
 * <p>Groups all seats belonging to the same row number,
 * used as part of {@link SeatMapResponseDTO}.</p>
 * 
 * @param row The row number
 * @param seats List of all seats in this row
 * 
 * @see SeatMapResponseDTO
 * @see SeatInfoDTO
 */
public record SeatMapRowDTO(
    int row,
    List<SeatInfoDTO> seats) {
}
