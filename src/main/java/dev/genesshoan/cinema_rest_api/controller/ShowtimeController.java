package dev.genesshoan.cinema_rest_api.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.genesshoan.cinema_rest_api.dto.showtime.ShowtimeCreateDTO;
import dev.genesshoan.cinema_rest_api.dto.showtime.ShowtimeResponseDTO;
import dev.genesshoan.cinema_rest_api.dto.showtime.ShowtimeUpdateDTO;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.service.ShowtimeService;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

/**
 * REST controller for managing showtimes in the cinema system.
 *
 * <p>
 * This controller provides endpoints for creating, retrieving, updating,
 * searching, and cancelling showtimes. Showtimes represent scheduled
 * movie screenings in specific rooms at specific times.
 * </p>
 *
 * <p>
 * All endpoints are mapped under the base path {@code /showtime}.
 * </p>
 *
 * @see ShowtimeService
 * @see ShowtimeCreateDTO
 * @see ShowtimeResponseDTO
 * @see ShowtimeUpdateDTO
 * @since 1.0.0
 */
@RestController
@AllArgsConstructor
@RequestMapping("/showtime")
@Validated
public class ShowtimeController {
  private final ShowtimeService showtimeService;

  /**
   * Creates a new showtime in the system.
   *
   * <p>
   * The showtime is validated to ensure:
   * </p>
   * <ul>
   * <li>The start time is before the end time</li>
   * <li>The referenced movie and room exist</li>
   * <li>There are no overlapping showtimes in the same room</li>
   * <li>All required fields are provided and valid</li>
   * </ul>
   *
   * @param showtimeCreateDTO the showtime data to create; must not be null
   * @return the created showtime with generated ID and additional metadata
   *
   * @see ShowtimeService#createShowtime(ShowtimeCreateDTO)
   */
  @PostMapping
  public ShowtimeResponseDTO createShowtime(@RequestBody ShowtimeCreateDTO showtimeCreateDTO) {
    return showtimeService.createShowtime(showtimeCreateDTO);
  }

  /**
   * Searches for showtimes based on multiple filter criteria.
   *
   * <p>
   * This endpoint allows filtering showtimes by date, room, movie, and status.
   * All parameters are optional and can be combined to narrow down results.
   * Results are paginated and can be sorted using Spring's Pageable parameters.
   * </p>
   *
   * <p>
   * Example query: {@code GET /showtime?dateTime=2026-01-21&roomId=1&movieId=5&status=SCHEDULED&page=0&size=10}
   * </p>
   *
   * @param dateTime the date to search for showtimes (day precision); can be null
   * @param roomId the room ID to filter by; can be null
   * @param movieId the movie ID to filter by; can be null
   * @param status the showtime status to filter by (SCHEDULED, COMPLETED, CANCELLED); can be null
   * @param pageable pagination and sorting parameters (page number, size, sort)
   * @return a page of showtimes matching the search criteria
   *
   * @see ShowtimeService#search(LocalDate, Long, Long, ShowtimeStatus, Pageable)
   */
  @GetMapping
  public Page<ShowtimeResponseDTO> search(
      @RequestParam LocalDate dateTime,
      @RequestParam Long roomId,
      @RequestParam Long movieId,
      @RequestParam ShowtimeStatus status,
      Pageable pageable) {
    return showtimeService.search(dateTime, roomId, movieId, status, pageable);
  }

  /**
   * Retrieves a showtime by its unique identifier.
   *
   * @param id the showtime ID; must be greater than 0
   * @return the showtime with the specified ID
   *
   * @see ShowtimeService#getShowtimeById(long)
   */
  @GetMapping("/{id}")
  public ShowtimeResponseDTO getShowtimeById(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    return showtimeService.getShowtimeById(id);
  }

  /**
   * Updates an existing showtime with new data.
   *
   * <p>
   * Allows updating showtime details such as start time, end time, room,
   * movie, base price, and status. The update is validated to ensure:
   * </p>
   * <ul>
   * <li>The showtime exists</li>
   * <li>The new times don't overlap with other showtimes in the same room</li>
   * <li>Start time is before end time</li>
   * <li>Referenced movie and room exist if changed</li>
   * </ul>
   *
   * @param id the ID of the showtime to update; must be greater than 0
   * @param showtimeUpdateDTO the updated showtime data; must not be null
   * @return the updated showtime
   *
   * @see ShowtimeService#updateShowtime(long, ShowtimeUpdateDTO)
   */
  @PutMapping("/{id}")
  public ShowtimeResponseDTO updateShowtime(
      @PathVariable(required = true) @Min(value = 1, message = "{id.min}") long id,
      @RequestBody ShowtimeUpdateDTO showtimeUpdateDTO) {
    return showtimeService.updateShowtime(id, showtimeUpdateDTO);
  }

  /**
   * Cancels a showtime by its unique identifier.
   *
   * <p>
   * This operation marks the showtime as CANCELLED rather than deleting it
   * from the database. This preserves historical records and allows for
   * auditing. Cancelled showtimes should not be displayed to users for
   * ticket purchases.
   * </p>
   *
   * <p>
   * Returns HTTP 204 (No Content) on successful cancellation.
   * </p>
   *
   * @param id the ID of the showtime to cancel; must be greater than 0
   *
   * @see ShowtimeService#cancelShowtime(long)
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancelShowtime(@PathVariable long id) {
    showtimeService.cancelShowtime(id);
  }
}
