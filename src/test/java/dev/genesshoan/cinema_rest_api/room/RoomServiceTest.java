package dev.genesshoan.cinema_rest_api.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.genesshoan.cinema_rest_api.dto.RoomRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.RoomResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.RoomMapper;
import dev.genesshoan.cinema_rest_api.repository.RoomRepository;
import dev.genesshoan.cinema_rest_api.service.RoomService;

/**
 * Unit tests for {@link RoomService}.
 *
 * <p>These tests validate the room service behavior: creating rooms, fetching
 * pages of rooms, retrieving by id, and updating. Each test isolates the
 * service from repository and mapper dependencies using Mockito.</p>
 */
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
  @Mock
  RoomRepository roomRepository;

  @Mock
  RoomMapper roomMapper;

  @InjectMocks
  RoomService roomService;

  Room room;

  RoomResponseDTO roomResponseDTO;

  RoomRequestDTO roomRequestDTO;

  Pageable pageable;

  @BeforeEach
  void setup() {
    room = new Room();
    room.setId(1L);
    room.setName("VIP Room");
    room.setRows(10);
    room.setSeatsPerRow(5);

    roomResponseDTO = new RoomResponseDTO(
        room.getId(),
        room.getName(),
        room.getRows(),
        room.getSeatsPerRow());

    roomRequestDTO = new RoomRequestDTO(
        room.getName(),
        room.getRows(),
        room.getSeatsPerRow());

    pageable = PageRequest.of(0, 2);
  }

  /**
   * Verifies that when a room with the provided name does not exist, the
   * service persists the new room and returns its DTO.
   */
  @Test
  @DisplayName("If a room with the same name does not exists, should save the room")
  void createMovie_WhenNotExists_ShouldSaveTheRoom() {
    when(roomMapper.toEntity(roomRequestDTO))
        .thenReturn(room);

    when(roomRepository.existsByName(room.getName()))
        .thenReturn(false);

    when(roomRepository.save(room))
        .thenReturn(room);

    when(roomMapper.toDto(room))
        .thenReturn(roomResponseDTO);

    RoomResponseDTO result = roomService.createRoom(roomRequestDTO);

    assertThat(result.id()).isEqualTo(room.getId());
    assertThat(result.name()).isEqualTo(room.getName());
    assertThat(result.rows()).isEqualTo(room.getRows());
    assertThat(result.seatsPerRow()).isEqualTo(room.getSeatsPerRow());

    verify(roomRepository, times(1)).save(room);
  }

  /**
   * Verifies that attempting to create a room whose name already exists
   * results in {@link ResourceAlreadyExistsException} and no save occurs.
   */
  @Test
  @DisplayName("If a room with the same name already exists, should throw an exception")
  void createMovie_WhenExists_ShouldThrowAnException() {
    when(roomMapper.toEntity(roomRequestDTO))
        .thenReturn(room);

    when(roomRepository.existsByName(room.getName()))
        .thenReturn(true);

    assertThatThrownBy(() -> roomService.createRoom(roomRequestDTO))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("Room with name VIP Room already exists");

    verify(roomRepository, never()).save(any(Room.class));
  }

  /**
   * Verifies that the service returns a page of room DTOs when rooms exist
   * in the repository.
   */
  @Test
  @DisplayName("Should return all rooms")
  void getAllRooms() {
    List<Room> rooms = List.of(room, room);
    Page<Room> page = new PageImpl<>(rooms, pageable, rooms.size());

    when(roomRepository.findAll(pageable))
        .thenReturn(page);
    when(roomMapper.toDto(any(Room.class)))
        .thenReturn(roomResponseDTO);

    Page<RoomResponseDTO> result = roomService.getAllRooms(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting(RoomResponseDTO::name).contains("VIP Room");
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(2);
  }

  /**
   * Verifies that when the repository contains no rooms, the service returns
   * an empty page and does not map any entities.
   */
  @Test
  @DisplayName("getAllRooms should return empty page when no rooms exist")
  void getAllRooms_WhenNoResults_ShouldReturnEmptyPage() {
    when(roomRepository.findAll(pageable)).thenReturn(Page.empty(pageable));

    Page<RoomResponseDTO> result = roomService.getAllRooms(pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();

    verify(roomRepository).findAll(pageable);
    verify(roomMapper, never()).toDto(any(Room.class));
  }

  /**
   * Verifies that a room can be retrieved by id and mapped to DTO when it
   * exists in the repository.
   */
  @Test
  @DisplayName("If exists a room with the given id, should return the room")
  void getRoomById_WhenExists_ShouldReturnARoom() {
    when(roomRepository.findById(room.getId()))
        .thenReturn(Optional.of(room));

    when(roomMapper.toDto(room))
        .thenReturn(roomResponseDTO);

    RoomResponseDTO result = roomService.getRoomById(room.getId());

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(room.getId());
    assertThat(result.name()).isEqualTo("VIP Room");
    assertThat(result.rows()).isEqualTo(10);
    assertThat(result.seatsPerRow()).isEqualTo(5);

    verify(roomRepository).findById(room.getId());
    verify(roomMapper).toDto(room);
  }

  /**
   * Verifies that requesting a non-existent room id results in
   * {@link ResourceNotFoundException}.
   */
  @Test
  @DisplayName("If room does not exist, should throw ResourceNotFoundException")
  void getRoomById_WhenNotExists_ShouldThrowException() {
    when(roomRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> roomService.getRoomById(1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Room with id 1 was not found");

    verify(roomMapper, never()).toDto(any());
  }

  /**
   * Verifies that updating an existing room without changing the name
   * persists the changes and returns a DTO.
   */
  @Test
  @DisplayName("If a room with the given id exists, and the name does not change, should update a room")
  void updateRoom_WhenExits_ShouldUpdateTheRoom() {
    when(roomRepository.findById(room.getId()))
        .thenReturn(Optional.of(room));

    when(roomRepository.save(any(Room.class)))
        .thenReturn(room);

    when(roomMapper.toDto(any(Room.class)))
        .thenReturn(roomResponseDTO);

    roomService.updateRoom(room.getId(), roomRequestDTO);

    verify(roomRepository, times(1)).save(any(Room.class));
    verify(roomMapper, times(1)).toDto(any(Room.class));
  }

  /**
   * Verifies that attempting to change the room name to one already in use
   * causes {@link ResourceAlreadyExistsException} and prevents persisting the
   * update.
   */
  @Test
  @DisplayName("If the new name is already in use, should throw an exception")
  void updateRoom_WhenNameIsAlredyInUse_ShouldThrowAnException() {
    when(roomRepository.findById(room.getId()))
        .thenReturn(Optional.of(room));

    roomRequestDTO = new RoomRequestDTO(
        "Distinct",
        12,
        10);

    when(roomRepository.existsByName(anyString()))
        .thenReturn(true);

    assertThatThrownBy(() -> roomService.updateRoom(room.getId(), roomRequestDTO))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("A room with name 'Distinct' already exits, the changes cannot be applied");

    verify(roomRepository, never()).save(any(Room.class));
    verify(roomMapper, never()).toDto(any(Room.class));
  }

  /**
   * Verifies that attempting to update a non-existent room id results in
   * {@link ResourceNotFoundException}.
   */
  @Test
  @DisplayName("If no room with the given id exists, should throw an exception")
  void updateRoom_WhenNotExists_ShouldThrownAnException() {
    when(roomRepository.findById(room.getId()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> roomService.updateRoom(room.getId(), roomRequestDTO))
        .isInstanceOf(ResourceNotFoundException.class);
  }
}
