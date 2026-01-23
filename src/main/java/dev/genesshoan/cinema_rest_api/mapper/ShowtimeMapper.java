package dev.genesshoan.cinema_rest_api.mapper;

import org.springframework.stereotype.Component;

import dev.genesshoan.cinema_rest_api.dto.showtime.ShowtimeCreateDTO;
import dev.genesshoan.cinema_rest_api.dto.showtime.ShowtimeResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Showtime;

/**
 * Simple mapper converting between {@link Showtime} entity and its DTOs.
 *
 * Assumptions:
 * - The provided entity and DTO fields are non-null where required.
 * - Association fields (room, movie) are accessed lazily; ensure the
 *   associations are initialized before calling toDto in transactional
 *   contexts or adapt as needed for projection-based queries.
 */
@Component
public class ShowtimeMapper {
  public ShowtimeResponseDTO toDto(Showtime showtime) {
    return new ShowtimeResponseDTO(
        showtime.getId(),
        showtime.getStartTime(),
        showtime.getEndTime(),
        showtime.getBasePrice(),
        showtime.getStatus(),
        showtime.getRoom().getName(),
        showtime.getMovie().getTitle());
  }

  public Showtime toEntity(ShowtimeCreateDTO showtimeCreateDTO) {
    Showtime showtime = new Showtime();
    showtime.setStartTime(showtimeCreateDTO.startTime());
    showtime.setEndTime(showtimeCreateDTO.endTime());
    return showtime;
  }
}
