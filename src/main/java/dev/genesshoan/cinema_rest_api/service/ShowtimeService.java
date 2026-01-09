package dev.genesshoan.cinema_rest_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.genesshoan.cinema_rest_api.dto.ShowtimeCreateDTO;
import dev.genesshoan.cinema_rest_api.dto.ShowtimeResponseDTO;
import dev.genesshoan.cinema_rest_api.dto.ShowtimeUpdateDTO;
import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.exception.InvalidRequestException;
import dev.genesshoan.cinema_rest_api.exception.OverlapingShowtimesException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.ShowtimeMapper;
import dev.genesshoan.cinema_rest_api.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service responsible for managing showtimes.
 *
 * Responsibilities (contract):
 * - createShowtime: validate input, ensure no overlapping showtimes, persist and
 *   associate the showtime with Movie and Room entities.
 * - search: retrieve paged showtimes filtered by date, room, movie and status.
 * - getShowtimeById / updateShowtime / cancelShowtime: typical CRUD-like
 *   retrieval and state transitions.
 *
 * Inputs/Outputs:
 * - Input DTOs are validated before persistence. Methods return DTOs intended
 *   for API responses.
 *
 * Error modes / Exceptions:
 * - ResourceNotFoundException: thrown when referenced entities do not exist.
 * - InvalidRequestException: thrown when business validation fails (e.g. start
 *   is not before end).
 * - OverlapingShowtimesException: thrown when a new showtime would overlap
 *   an existing scheduled showtime in the same room.
 *
 * Thread-safety: this service delegates persistence to Spring Data repositories
 * and does not maintain mutable shared state.
 */
@Service
@RequiredArgsConstructor
public class ShowtimeService {
  private final ShowtimeRepository showtimeRepository;
  private final MovieService movieService;
  private final RoomService roomService;
  private final ShowtimeMapper showtimeMapper;

  /**
   * Create and persist a new showtime.
   *
   * Steps performed:
   * 1. Validate that start is before end.
   * 2. Check for overlapping showtimes in the same room (scheduled status).
   * 3. Map DTO to entity and associate with Movie and Room.
   * 4. Persist and return a response DTO.
   *
   * @param showtimeCreateDTO DTO containing the showtime creation data (start,
   *                         end, base price, movie id, room id)
   * @return ShowtimeResponseDTO representation of the newly created showtime
   * @throws InvalidRequestException when start is not before end
   * @throws OverlapingShowtimesException when another scheduled showtime already
   *                                     occupies the given time slot in the same room
   * @throws ResourceNotFoundException when the referenced movie or room does not exist
   */
  public ShowtimeResponseDTO createShowtime(ShowtimeCreateDTO showtimeCreateDTO) {

    validateStartBeforeEnd(showtimeCreateDTO.startTime(), showtimeCreateDTO.endTime());

    if (showtimeRepository.existsOverlappingShowtime(
        showtimeCreateDTO.roomId(), showtimeCreateDTO.startTime(),
        showtimeCreateDTO.endTime(),
        ShowtimeStatus.SCHEDULED)) {
      throw new OverlapingShowtimesException("A showtime already exists in this room at the specified time.");
    }

    Showtime showtime = showtimeMapper.toEntity(showtimeCreateDTO);

    movieService.getEntityById(showtimeCreateDTO.movieId())
        .addShowtime(showtime);

    roomService.getEntityById(showtimeCreateDTO.roomId())
        .addShowtime(showtime);

    return showtimeMapper.toDto(
        showtimeRepository.save(showtime));
  }

  /**
   * Search for showtimes by date range, room, movie and status using a
   * paginated result.
   *
   * This method converts the provided LocalDate into a 24-hour window starting
   * at the beginning of the day (inclusive) and ending at the beginning of the
   * next day (exclusive).
   *
   * @param dateTime date used to build the search window (day precision)
   * @param roomId optional room id to filter results
   * @param movieId optional movie id to filter results
   * @param status optional showtime status to filter results
   * @param pageable pagination information
   * @return page of ShowtimeResponseDTO matching the provided criteria
   */
  public Page<ShowtimeResponseDTO> search(
      LocalDate dateTime,
      Long roomId,
      Long movieId,
      ShowtimeStatus status,
      Pageable pageable) {
    return showtimeRepository.search(
        dateTime.atStartOfDay(),
        dateTime.plusDays(1).atStartOfDay(),
        roomId,
        movieId,
        status,
        pageable)
        .map(showtimeMapper::toDto);
  }

  /**
   * Retrieve a showtime by its identifier.
   *
   * @param id the showtime id
   * @return ShowtimeResponseDTO of the requested showtime
   * @throws ResourceNotFoundException if the showtime does not exist
   */
  public ShowtimeResponseDTO getShowtimeById(long id) {
    Showtime showtime = showtimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Showtime with id '" + id + "' does not exist"));

    return showtimeMapper.toDto(showtime);
  }

  /**
   * Update a show's scheduling information and base price.
   *
   * This method validates that the updated start and end times are consistent
   * and will throw {@link InvalidRequestException} if they are not. It does not
   * check for overlapping showtimes on update — if that behavior is required
   * it should be added here.
   *
   * @param id the showtime id to update
   * @param showtimeUpdateDTO DTO containing new start, end and base price
   * @return updated ShowtimeResponseDTO
   * @throws ResourceNotFoundException if the showtime does not exist
   * @throws InvalidRequestException if the provided times are invalid
   */
  public ShowtimeResponseDTO updateShowtime(long id, ShowtimeUpdateDTO showtimeUpdateDTO) {
    Showtime existing = showtimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Showtime with id '" + id + "' does not exist"));

    validateStartBeforeEnd(showtimeUpdateDTO.startTime(), showtimeUpdateDTO.endTime());

    existing.setStartTime(showtimeUpdateDTO.startTime());
    existing.setEndTime(showtimeUpdateDTO.endTime());
    existing.setBasePrice(showtimeUpdateDTO.basePrice());

    return showtimeMapper.toDto(existing);
  }

  /**
   * Cancel an existing showtime by setting its status to CANCELLED.
   *
   * Note: There is a TODO to check for existing sales before cancelling.
   *
   * @param id identifier of the showtime to cancel
   * @throws ResourceNotFoundException if the showtime does not exist
   */
  public void cancelShowtime(long id) {
    Showtime showtime = showtimeRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Showtime with id '" + id + "' does not exist"));

    // TODO: Check if it does not has sales first
    showtime.setStatus(ShowtimeStatus.CANCELLED);
  }

  /**
   * Validate that the provided start and end times are not null and that the
   * start time is strictly before the end time.
   *
   * Behavior: if either parameter is null the method returns without throwing
   * because DTO-level validation (@NotNull) is expected to enforce presence.
   *
   * @param start start time to validate
   * @param end end time to validate
   * @throws InvalidRequestException when start is not strictly before end
   */
  private void validateStartBeforeEnd(LocalDateTime start, LocalDateTime end) {
    if (start == null || end == null) {
      return; // null checks remain handled by @NotNull in DTOs
    }

    if (!start.isBefore(end)) {
      throw new InvalidRequestException("Start time must be before end time");
    }
  }
}
