package dev.genesshoan.cinema_rest_api.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.genesshoan.cinema_rest_api.dto.seat.SeatInfoDTO;
import dev.genesshoan.cinema_rest_api.dto.seat.SeatMapResponseDTO;
import dev.genesshoan.cinema_rest_api.dto.seat.SeatMapRowDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;
import dev.genesshoan.cinema_rest_api.entity.Seat;
import dev.genesshoan.cinema_rest_api.entity.SeatStatus;
import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.repository.SeatRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for managing seat-related business logic.
 * 
 * <p>
 * Provides functionality to retrieve seat maps, check availability,
 * and calculate occupancy rates for showtimes.
 * </p>
 * 
 * @see Seat
 * @see SeatRepository
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SeatService {
  private final SeatRepository seatRepository;

  /**
   * Retrieves the complete seat map for a given showtime.
   * 
   * <p>
   * Returns all seats organized by rows, showing their current status
   * (AVAILABLE/SOLD) and the overall occupancy percentage.
   * </p>
   * 
   * @param showtimeId The ID of the showtime
   * @return A {@link SeatMapResponseDTO} containing the seat map and occupancy
   *         data
   */
  public SeatMapResponseDTO getSeatMap(long showtimeId) {
    List<Seat> seats = seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(showtimeId);

    Map<Integer, List<SeatInfoDTO>> seatsByRow = seats.stream()
        .collect(Collectors.groupingBy(
            Seat::getRowNumber,
            Collectors.mapping(
                (s) -> new SeatInfoDTO(s.getRowNumber(), s.getSeatNumber(), s.getStatus()),
                Collectors.toList())));

    List<SeatMapRowDTO> rows = seatsByRow.entrySet().stream()
        .map((r) -> new SeatMapRowDTO(r.getKey(), r.getValue()))
        .sorted(Comparator.comparing(SeatMapRowDTO::row))
        .toList();

    long soldSeats = seats.stream()
        .filter((s) -> s.getStatus() == SeatStatus.SOLD)
        .count();

    double occupancyPercentage = seats.isEmpty() ? 0.0 : (soldSeats * 100.0) / seats.size();

    return new SeatMapResponseDTO(showtimeId,
        rows,
        occupancyPercentage);
  }

  /**
   * Creates all seats for a showtime based on the room's configuration.
   * 
   * <p>
   * Generates seats for each row and position according to the room's
   * rows and seatsPerRow properties. All seats are initially set to AVAILABLE
   * status.
   * </p>
   * 
   * @param showtime The showtime for which to create seats
   * @param room     The room containing the configuration (rows, seatsPerRow)
   */
  @Transactional
  public void createSeatsForShowtime(Showtime showtime, Room room) {
    List<Seat> seats = new ArrayList<>();

    for (int row = 1; row <= room.getRows(); row++) {
      for (int seatNumber = 1; seatNumber <= room.getSeatsPerRow(); seatNumber++) {
        Seat seat = new Seat();
        seat.setRowNumber(row);
        seat.setSeatNumber(seatNumber);
        seat.setStatus(SeatStatus.AVAILABLE);
        seat.setShowtime(showtime);
        seats.add(seat);
      }
    }

    seatRepository.saveAll(seats);
  }
}
