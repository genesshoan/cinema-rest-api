package dev.genesshoan.cinema_rest_api.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.genesshoan.cinema_rest_api.dto.seat.SeatMapResponseDTO;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.service.SeatService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing cinema seats.
 *
 * <p>
 * This controller provides endpoints for retrieving seat information and
 * availability for specific showtimes. All endpoints return JSON responses and
 * follow RESTful conventions.
 * </p>
 *
 * <p>
 * Base URL: {@code /seats}
 * </p>
 *
 * <p>
 * Supported operations:
 * <ul>
 * <li>Retrieve seat map and availability for a showtime</li>
 * </ul>
 * </p>
 *
 * @see SeatService
 * @see SeatMapResponseDTO
 * @since 1.0.0
 */
@RestController
@RequestMapping("/seats")
@Validated
@RequiredArgsConstructor
public class SeatController {
  private final SeatService seatService;

  /**
   * Retrieves the seat map and availability for a specific showtime.
   *
   * <p>
   * Returns a complete seat layout showing all seats and their current
   * availability status (available, occupied, reserved) for the given showtime.
   * </p>
   *
   * @param showtimeId the showtime ID, must be greater than 0
   * @return the seat map with availability information
   * @throws ResourceNotFoundException if no showtime with the given ID exists
   *
   * @see SeatService#getSeatMap(long)
   */
  @GetMapping("/{showtimeId}")
  public SeatMapResponseDTO getSeatMap(
      @PathVariable @Min(value = 1, message = "{id.min}") long showtimeId) {
    return seatService.getSeatMap(showtimeId);
  }
}
