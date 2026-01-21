package dev.genesshoan.cinema_rest_api.service;

import java.util.Objects;

import dev.genesshoan.cinema_rest_api.exception.ResourceInUseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.genesshoan.cinema_rest_api.dto.RoomRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.RoomResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.RoomMapper;
import dev.genesshoan.cinema_rest_api.repository.RoomRepository;
import lombok.RequiredArgsConstructor;

/**
 * Service layer for room business logic.
 * 
 * This service handles all room-related operations including creation,
 * retrieval, updating, and searching. It enforces business rules such as
 * preventing duplicate room with the same name.
 * 
 * <p>
 * This service is transactional and performs validation before
 * persisting data to the database.
 * </p>
 * 
 * @see RoomRepository
 * @see RoomMapper
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;

  /**
   * Creates a new room in the database.
   * 
   * This method validates that no room with the same name exists before create a
   * new one.
   *
   * @param roomRequestDTO the room data to CreateKeySecondPass
   * @return the created room data
   * @throws ResourceAlreadyExistsException if a movie with the given 'id' already
   *                                        exists
   */
  @Transactional
  public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {
    Room room = roomMapper.toEntity(roomRequestDTO);

    if (roomRepository.existsByName(room.getName())) {
      throw new ResourceAlreadyExistsException("Room with name " + room.getName() + " already exists");
    }

    return roomMapper.toDto(
        roomRepository.save(room));
  }

  /**
   * Retrieves all rooms in room's repository.
   *
   * @param pageable pagination and sorting parameters
   * @return a page with all rooms in the system
   */
  public Page<RoomResponseDTO> getAllRooms(Pageable pageable) {
    return roomRepository.findAll(pageable).map(roomMapper::toDto);
  }

  /**
   * Retrieves a room by its id.
   *
   * @param id the room 'id' to search for
   * @return the room with the specified 'id'
   * @throws ResourceNotFoundException if no room with the given 'id' does not
   *                                   exists
   */
  public RoomResponseDTO getRoomById(long id) {
    Room room = roomRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " was not found"));

    return roomMapper.toDto(room);
  }

  public Room getEntityById(long id) {
    return roomRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " was not found"));
  }

  /**
   * Updates an existing room.
   *
   * All fields of the room are replaced with provided data.
   * The id cannot be changed
   * 
   * @param id             the 'id' of the room to be changed
   * @param roomRequestDTO the new room data
   * @return the updated room data
   * @throws ResourceNotFoundException      if no room with 'id' does not exists
   * @throws ResourceAlreadyExistsException if already exists a room with the
   *                                        given new name
   */
  @Transactional
  public RoomResponseDTO updateRoom(long id, RoomRequestDTO roomRequestDTO) {
    Room existing = roomRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " was not found"));

    boolean nameIsChanging = !Objects.equals(existing.getName(), roomRequestDTO.name());

    if (nameIsChanging && roomRepository.existsByName(roomRequestDTO.name())) {
      throw new ResourceAlreadyExistsException(
          String.format("A room with name '%s' already exits, the changes cannot be applied", roomRequestDTO.name()));
    }

    existing.setName(roomRequestDTO.name());
    existing.setRows(roomRequestDTO.rows());
    existing.setSeatsPerRow(roomRequestDTO.seatsPerRow());

    return roomMapper.toDto(
        roomRepository.save(existing));
  }

  /**
   * Soft deletes a room by its unique identifier.
   *
   * <p>
   * This method marks the room as inactive (soft delete) rather than
   * physically removing it from the database. This preserves historical
   * data and references from existing showtimes.
   * </p>
   *
   * <p>
   * A room cannot be deleted if it has active (scheduled) showtimes.
   * This prevents inconsistent state where showtimes reference deleted rooms.
   * </p>
   *
   * @param id the ID of the room to delete.
   * @throws ResourceNotFoundException if no room with the given ID exists.
   * @throws ResourceInUseException if the room has active showtimes.
   */
  @Transactional
  public void deleteRoomById(long id) {
    Room room = roomRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room with id '" + id + "' does not exist"));

    if (roomRepository.hasActiveShowtimes(id)) {
      throw new ResourceInUseException(
          "Cannot delete room with id '" + id + "' because it has active (scheduled) showtimes");
    }

    room.setActive(false);
    roomRepository.save(room);
  }
}
