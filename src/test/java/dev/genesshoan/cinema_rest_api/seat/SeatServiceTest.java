package dev.genesshoan.cinema_rest_api.seat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.genesshoan.cinema_rest_api.dto.seat.SeatMapResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;
import dev.genesshoan.cinema_rest_api.entity.Seat;
import dev.genesshoan.cinema_rest_api.entity.SeatStatus;
import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.repository.SeatRepository;
import dev.genesshoan.cinema_rest_api.service.SeatService;

/**
 * Unit tests for {@link SeatService}.
 *
 * These tests verify the service behavior for retrieving seat maps and creating
 * seats for showtimes. Mockito is used to isolate the service from its
 * dependencies and each test documents the expected behavior with Javadoc.
 */
@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

  @Mock
  private SeatRepository seatRepository;

  @InjectMocks
  private SeatService seatService;

  @Captor
  private ArgumentCaptor<List<Seat>> seatListCaptor;

  private Showtime showtime;
  private Room room;

  @BeforeEach
  void setUp() {
    showtime = new Showtime();
    showtime.setId(100L);

    room = new Room();
    room.setId(10L);
    room.setName("Room A");
    room.setRows(5);
    room.setSeatsPerRow(10);
  }

  /**
   * Verifies that getSeatMap returns a complete seat map with all seats organized by rows.
   */
  @Test
  @DisplayName("getSeatMap - with seats: should return organized seat map")
  void getSeatMap_WhenSeatsExist_ShouldReturnOrganizedMap() {
    List<Seat> seats = Arrays.asList(
        createSeat(1L, 1, 1, SeatStatus.AVAILABLE),
        createSeat(2L, 1, 2, SeatStatus.SOLD),
        createSeat(3L, 2, 1, SeatStatus.AVAILABLE),
        createSeat(4L, 2, 2, SeatStatus.AVAILABLE));

    when(seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(100L)).thenReturn(seats);

    SeatMapResponseDTO result = seatService.getSeatMap(100L);

    assertThat(result).isNotNull();
    assertThat(result.showtimeId()).isEqualTo(100L);
    assertThat(result.rows()).hasSize(2);
    assertThat(result.rows().get(0).row()).isEqualTo(1);
    assertThat(result.rows().get(0).seats()).hasSize(2);
    assertThat(result.rows().get(1).row()).isEqualTo(2);
    assertThat(result.rows().get(1).seats()).hasSize(2);
    assertThat(result.occupancyPercentage()).isEqualTo(25.0);

    verify(seatRepository).findByIdOrderByRowNumberAscSeatNumberAsc(100L);
  }

  /**
   * Verifies that getSeatMap calculates occupancy percentage correctly.
   */
  @Test
  @DisplayName("getSeatMap - occupancy: should calculate percentage correctly")
  void getSeatMap_ShouldCalculateOccupancyCorrectly() {
    List<Seat> seats = Arrays.asList(
        createSeat(1L, 1, 1, SeatStatus.SOLD),
        createSeat(2L, 1, 2, SeatStatus.SOLD),
        createSeat(3L, 1, 3, SeatStatus.AVAILABLE),
        createSeat(4L, 1, 4, SeatStatus.AVAILABLE));

    when(seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(100L)).thenReturn(seats);

    SeatMapResponseDTO result = seatService.getSeatMap(100L);

    assertThat(result.occupancyPercentage()).isEqualTo(50.0);
  }

  /**
   * Verifies that getSeatMap returns 0% occupancy when all seats are available.
   */
  @Test
  @DisplayName("getSeatMap - all available: should return 0% occupancy")
  void getSeatMap_WhenAllAvailable_ShouldReturnZeroOccupancy() {
    List<Seat> seats = Arrays.asList(
        createSeat(1L, 1, 1, SeatStatus.AVAILABLE),
        createSeat(2L, 1, 2, SeatStatus.AVAILABLE));

    when(seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(100L)).thenReturn(seats);

    SeatMapResponseDTO result = seatService.getSeatMap(100L);

    assertThat(result.occupancyPercentage()).isEqualTo(0.0);
  }

  /**
   * Verifies that getSeatMap returns 100% occupancy when all seats are sold.
   */
  @Test
  @DisplayName("getSeatMap - all sold: should return 100% occupancy")
  void getSeatMap_WhenAllSold_ShouldReturnFullOccupancy() {
    List<Seat> seats = Arrays.asList(
        createSeat(1L, 1, 1, SeatStatus.SOLD),
        createSeat(2L, 1, 2, SeatStatus.SOLD));

    when(seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(100L)).thenReturn(seats);

    SeatMapResponseDTO result = seatService.getSeatMap(100L);

    assertThat(result.occupancyPercentage()).isEqualTo(100.0);
  }

  /**
   * Verifies that getSeatMap handles empty seat list correctly.
   */
  @Test
  @DisplayName("getSeatMap - no seats: should return empty map with 0% occupancy")
  void getSeatMap_WhenNoSeats_ShouldReturnEmptyMap() {
    when(seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(100L)).thenReturn(Collections.emptyList());

    SeatMapResponseDTO result = seatService.getSeatMap(100L);

    assertThat(result).isNotNull();
    assertThat(result.showtimeId()).isEqualTo(100L);
    assertThat(result.rows()).isEmpty();
    assertThat(result.occupancyPercentage()).isEqualTo(0.0);
  }

  /**
   * Verifies that getSeatMap sorts rows correctly.
   */
  @Test
  @DisplayName("getSeatMap - row order: should sort rows in ascending order")
  void getSeatMap_ShouldSortRowsAscending() {
    List<Seat> seats = Arrays.asList(
        createSeat(1L, 3, 1, SeatStatus.AVAILABLE),
        createSeat(2L, 1, 1, SeatStatus.AVAILABLE),
        createSeat(3L, 2, 1, SeatStatus.AVAILABLE));

    when(seatRepository.findByIdOrderByRowNumberAscSeatNumberAsc(100L)).thenReturn(seats);

    SeatMapResponseDTO result = seatService.getSeatMap(100L);

    assertThat(result.rows()).hasSize(3);
    assertThat(result.rows().get(0).row()).isEqualTo(1);
    assertThat(result.rows().get(1).row()).isEqualTo(2);
    assertThat(result.rows().get(2).row()).isEqualTo(3);
  }

  /**
   * Verifies that createSeatsForShowtime creates the correct number of seats.
   */
  @Test
  @DisplayName("createSeatsForShowtime - happy path: should create all seats")
  void createSeatsForShowtime_ShouldCreateAllSeats() {
    room.setRows(3);
    room.setSeatsPerRow(4);

    seatService.createSeatsForShowtime(showtime, room);

    verify(seatRepository).saveAll(seatListCaptor.capture());
    List<Seat> savedSeats = seatListCaptor.getValue();

    assertThat(savedSeats).hasSize(12);
  }

  /**
   * Verifies that createSeatsForShowtime sets all seats to AVAILABLE status.
   */
  @Test
  @DisplayName("createSeatsForShowtime - status: should set all seats to AVAILABLE")
  void createSeatsForShowtime_ShouldSetAllSeatsAvailable() {
    room.setRows(2);
    room.setSeatsPerRow(2);

    seatService.createSeatsForShowtime(showtime, room);

    verify(seatRepository).saveAll(seatListCaptor.capture());
    List<Seat> savedSeats = seatListCaptor.getValue();

    assertThat(savedSeats).allMatch(seat -> seat.getStatus() == SeatStatus.AVAILABLE);
  }

  /**
   * Verifies that createSeatsForShowtime assigns correct row and seat numbers.
   */
  @Test
  @DisplayName("createSeatsForShowtime - numbering: should assign correct row and seat numbers")
  void createSeatsForShowtime_ShouldAssignCorrectNumbers() {
    room.setRows(2);
    room.setSeatsPerRow(3);

    seatService.createSeatsForShowtime(showtime, room);

    verify(seatRepository).saveAll(seatListCaptor.capture());
    List<Seat> savedSeats = seatListCaptor.getValue();

    assertThat(savedSeats).hasSize(6);
    assertThat(savedSeats.stream().filter(s -> s.getRowNumber() == 1)).hasSize(3);
    assertThat(savedSeats.stream().filter(s -> s.getRowNumber() == 2)).hasSize(3);
    assertThat(savedSeats.stream().filter(s -> s.getSeatNumber() == 1)).hasSize(2);
    assertThat(savedSeats.stream().filter(s -> s.getSeatNumber() == 2)).hasSize(2);
    assertThat(savedSeats.stream().filter(s -> s.getSeatNumber() == 3)).hasSize(2);
  }

  /**
   * Verifies that createSeatsForShowtime associates all seats with the showtime.
   */
  @Test
  @DisplayName("createSeatsForShowtime - association: should associate all seats with showtime")
  void createSeatsForShowtime_ShouldAssociateSeatsWithShowtime() {
    room.setRows(2);
    room.setSeatsPerRow(2);

    seatService.createSeatsForShowtime(showtime, room);

    verify(seatRepository).saveAll(seatListCaptor.capture());
    List<Seat> savedSeats = seatListCaptor.getValue();

    assertThat(savedSeats).allMatch(seat -> seat.getShowtime() == showtime);
  }

  /**
   * Verifies that createSeatsForShowtime handles a room with no seats.
   */
  @Test
  @DisplayName("createSeatsForShowtime - empty room: should handle zero seats")
  void createSeatsForShowtime_WhenNoSeats_ShouldSaveEmptyList() {
    room.setRows(0);
    room.setSeatsPerRow(0);

    seatService.createSeatsForShowtime(showtime, room);

    verify(seatRepository).saveAll(seatListCaptor.capture());
    List<Seat> savedSeats = seatListCaptor.getValue();

    assertThat(savedSeats).isEmpty();
  }

  /**
   * Verifies that createSeatsForShowtime handles a single seat room.
   */
  @Test
  @DisplayName("createSeatsForShowtime - single seat: should create one seat")
  void createSeatsForShowtime_WhenSingleSeat_ShouldCreateOneSeat() {
    room.setRows(1);
    room.setSeatsPerRow(1);

    seatService.createSeatsForShowtime(showtime, room);

    verify(seatRepository).saveAll(seatListCaptor.capture());
    List<Seat> savedSeats = seatListCaptor.getValue();

    assertThat(savedSeats).hasSize(1);
    assertThat(savedSeats.get(0).getRowNumber()).isEqualTo(1);
    assertThat(savedSeats.get(0).getSeatNumber()).isEqualTo(1);
  }

  private Seat createSeat(Long id, int rowNumber, int seatNumber, SeatStatus status) {
    Seat seat = new Seat();
    seat.setId(id);
    seat.setRowNumber(rowNumber);
    seat.setSeatNumber(seatNumber);
    seat.setStatus(status);
    seat.setShowtime(showtime);
    return seat;
  }
}
