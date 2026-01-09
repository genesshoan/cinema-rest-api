package dev.genesshoan.cinema_rest_api.showtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.genesshoan.cinema_rest_api.dto.ShowtimeCreateDTO;
import dev.genesshoan.cinema_rest_api.dto.ShowtimeResponseDTO;
import dev.genesshoan.cinema_rest_api.dto.ShowtimeUpdateDTO;
import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.entity.Room;
import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.exception.InvalidRequestException;
import dev.genesshoan.cinema_rest_api.exception.OverlapingShowtimesException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.ShowtimeMapper;
import dev.genesshoan.cinema_rest_api.repository.ShowtimeRepository;
import dev.genesshoan.cinema_rest_api.service.ShowtimeService;

/**
 * Unit tests for {@link ShowtimeService}.
 *
 * These tests verify the service behavior for creating, retrieving, updating
 * and cancelling showtimes. Mockito is used to isolate the service from its
 * dependencies and each test documents the expected behavior with Javadoc.
 */
@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceTest {

  @Mock
  private ShowtimeRepository showtimeRepository;

  @Mock
  private dev.genesshoan.cinema_rest_api.service.MovieService movieService;

  @Mock
  private dev.genesshoan.cinema_rest_api.service.RoomService roomService;

  @Mock
  private ShowtimeMapper showtimeMapper;

  @InjectMocks
  private ShowtimeService showtimeService;

  private ShowtimeCreateDTO createDTO;
  private ShowtimeUpdateDTO updateDTO;
  private ShowtimeResponseDTO responseDTO;
  private Showtime showtime;
  private Movie movie;
  private Room room;

  @BeforeEach
  void setUp() {
    movie = new Movie();
    movie.setId(10L);
    movie.setTitle("Test Movie");

    room = new Room();
    room.setId(20L);
    room.setName("Room A");

    LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime end = start.plusHours(2);

    createDTO = new ShowtimeCreateDTO(start, end, new BigDecimal("5.00"), room.getId(), movie.getId());
    updateDTO = new ShowtimeUpdateDTO(start.plusDays(1), end.plusDays(1), new BigDecimal("6.50"));

    showtime = new Showtime();
    showtime.setId(100L);
    showtime.setStartTime(start);
    showtime.setEndTime(end);
    showtime.setBasePrice(new BigDecimal("5.00"));
    showtime.setStatus(ShowtimeStatus.SCHEDULED);
    showtime.setMovie(movie);
    showtime.setRoom(room);

    responseDTO = new ShowtimeResponseDTO(
        showtime.getId(),
        showtime.getStartTime(),
        showtime.getEndTime(),
        showtime.getBasePrice(),
        showtime.getStatus(),
        room.getName(),
        movie.getTitle());
  }

  /**
   * Verifies that creating a valid showtime persists it and returns a
   * {@link ShowtimeResponseDTO}.
   */
  @Test
  @DisplayName("createShowtime - happy path: should save and return DTO")
  void createShowtime_WhenValid_ShouldSaveAndReturnDto() {
    when(showtimeRepository.existsOverlappingShowtime(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(ShowtimeStatus.class)))
        .thenReturn(false);
    when(showtimeMapper.toEntity(createDTO)).thenReturn(showtime);
    when(movieService.getEntityById(movie.getId())).thenReturn(movie);
    when(roomService.getEntityById(room.getId())).thenReturn(room);
    when(showtimeRepository.save(showtime)).thenReturn(showtime);
    when(showtimeMapper.toDto(showtime)).thenReturn(responseDTO);

    ShowtimeResponseDTO result = showtimeService.createShowtime(createDTO);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(showtime.getId());
    assertThat(result.roomName()).isEqualTo(room.getName());
    assertThat(result.movieTitle()).isEqualTo(movie.getTitle());

    verify(showtimeRepository).existsOverlappingShowtime(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(ShowtimeStatus.class));
    verify(showtimeRepository).save(showtime);
  }

  /**
   * Verifies that attempting to create an overlapping showtime throws
   * {@link OverlapingShowtimesException} and does not persist the entity.
   */
  @Test
  @DisplayName("createShowtime - overlapping: should throw OverlapingShowtimesException")
  void createShowtime_WhenOverlapping_ShouldThrowException() {
    when(showtimeRepository.existsOverlappingShowtime(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class), any(ShowtimeStatus.class)))
        .thenReturn(true);

    assertThatThrownBy(() -> showtimeService.createShowtime(createDTO))
        .isInstanceOf(OverlapingShowtimesException.class)
        .hasMessageContaining("A showtime already exists");

    verify(showtimeRepository, never()).save(any(Showtime.class));
  }

  /**
   * Verifies that creating a showtime with invalid start/end times (start not
   * before end) throws {@link InvalidRequestException}.
   */
  @Test
  @DisplayName("createShowtime - invalid times: should throw InvalidRequestException")
  void createShowtime_WhenStartNotBeforeEnd_ShouldThrowInvalidRequest() {
    // craft DTO with start after end
    LocalDateTime start = LocalDateTime.now().plusDays(2);
    LocalDateTime end = start.minusHours(1);
    ShowtimeCreateDTO bad = new ShowtimeCreateDTO(start, end, new BigDecimal("4.00"), room.getId(), movie.getId());

    assertThatThrownBy(() -> showtimeService.createShowtime(bad))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessageContaining("Start time must be before end time");

    verify(showtimeRepository, never()).save(any(Showtime.class));
  }

  /**
   * Verifies that retrieving a showtime by id returns a DTO when the entity
   * exists.
   */
  @Test
  @DisplayName("getShowtimeById - exists: should return DTO")
  void getShowtimeById_WhenExists_ShouldReturnDto() {
    when(showtimeRepository.findById(100L)).thenReturn(Optional.of(showtime));
    when(showtimeMapper.toDto(showtime)).thenReturn(responseDTO);

    ShowtimeResponseDTO result = showtimeService.getShowtimeById(100L);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(100L);

    verify(showtimeRepository).findById(100L);
  }

  /**
   * Verifies that requesting a non-existent showtime id throws
   * {@link ResourceNotFoundException}.
   */
  @Test
  @DisplayName("getShowtimeById - not exists: should throw ResourceNotFoundException")
  void getShowtimeById_WhenNotExists_ShouldThrow() {
    when(showtimeRepository.findById(100L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> showtimeService.getShowtimeById(100L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Showtime with id '100' does not exist");
  }

  /**
   * Verifies that updating an existing showtime applies changes and returns
   * the updated DTO.
   */
  @Test
  @DisplayName("updateShowtime - exists: should update and return DTO")
  void updateShowtime_WhenExists_ShouldUpdate() {
    when(showtimeRepository.findById(100L)).thenReturn(Optional.of(showtime));
    when(showtimeMapper.toDto(showtime)).thenReturn(responseDTO);

    ShowtimeResponseDTO result = showtimeService.updateShowtime(100L, updateDTO);

    assertThat(result).isNotNull();
    verify(showtimeRepository).findById(100L);
  }

  /**
   * Verifies that cancelling an existing showtime sets its status to
   * {@link ShowtimeStatus#CANCELLED}.
   */
  @Test
  @DisplayName("cancelShowtime - exists: should set status to CANCELLED")
  void cancelShowtime_WhenExists_ShouldSetCancelled() {
    when(showtimeRepository.findById(100L)).thenReturn(Optional.of(showtime));

    showtimeService.cancelShowtime(100L);

    assertThat(showtime.getStatus()).isEqualTo(ShowtimeStatus.CANCELLED);
    verify(showtimeRepository).findById(100L);
  }

  /**
   * Verifies that cancelling a non-existent showtime throws
   * {@link ResourceNotFoundException}.
   */
  @Test
  @DisplayName("cancelShowtime - not exists: should throw ResourceNotFoundException")
  void cancelShowtime_WhenNotExists_ShouldThrow() {
    when(showtimeRepository.findById(100L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> showtimeService.cancelShowtime(100L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Showtime with id '100' does not exist");
  }
}

