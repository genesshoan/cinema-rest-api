package dev.genesshoan.cinema_rest_api.mapper;

import org.springframework.stereotype.Component;

import dev.genesshoan.cinema_rest_api.dto.seat.SeatRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.seat.SeatResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Seat;

/**
 * Mapper responsible for converting between Seat entity and its DTO representations.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Convert a {@link Seat} entity to {@link SeatResponseDTO} for API responses.</li>
 *   <li>Convert a {@link SeatRequestDTO} to a {@link Seat} entity for persistence.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Note: these methods expect non-null inputs. If a null input is provided the
 * method will result in a {@link NullPointerException} at runtime.
 * Callers should validate inputs before invoking the mapper or handle the
 * exception appropriately.
 * </p>
 *
 * <p>
 * The {@code toDto} method assumes the showtime association is initialized.
 * Ensure it is loaded in a transactional context before calling this method.
 * </p>
 */
@Component
public class SeatMapper {
  /**
   * Convert a {@link Seat} entity into a {@link SeatResponseDTO}.
   *
   * @param seat the entity to convert (must be non-null)
   * @return a SeatResponseDTO containing the entity's id, rowNumber, seatNumber, status, and showtimeId
   * @throws NullPointerException if {@code seat} is null or if its showtime association is not initialized
   */
  public SeatResponseDTO toDto(Seat seat) {
    return new SeatResponseDTO(
        seat.getId(),
        seat.getRowNumber(),
        seat.getSeatNumber(),
        seat.getStatus(),
        seat.getShowtime().getId());
  }

  /**
   * Create a new {@link Seat} entity from the provided {@link SeatRequestDTO}.
   *
   * <p>The returned entity is a fresh instance with fields copied from the
   * request DTO. It does not set persistence-related fields like {@code id}.
   * The showtime association must be set separately by the service layer.
   * </p>
   *
   * @param seatRequestDTO the DTO containing seat data (must be non-null)
   * @return a new Seat entity populated from the request DTO (without showtime association)
   * @throws NullPointerException if {@code seatRequestDTO} is null
   */
  public Seat toEntity(SeatRequestDTO seatRequestDTO) {
    Seat seat = new Seat();
    seat.setRowNumber(seatRequestDTO.rowNumber());
    seat.setSeatNumber(seatRequestDTO.seatNumber());
    seat.setStatus(seatRequestDTO.status());

    return seat;
  }
}
