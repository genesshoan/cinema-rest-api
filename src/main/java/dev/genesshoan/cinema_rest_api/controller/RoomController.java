package dev.genesshoan.cinema_rest_api.controller;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.genesshoan.cinema_rest_api.dto.RoomRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.RoomResponseDTO;
import dev.genesshoan.cinema_rest_api.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing cinema rooms.
 *
 * <p>
 * This controller exposes HTTP endpoints to create, retrieve (single and
 * paginated), and update room resources used by the cinema system. All
 * endpoints accept and return JSON and follow RESTful conventions.
 * </p>
 *
 * <p>
 * Base URL: {@code /rooms}
 * </p>
 *
 * <p>
 * Supported operations:
 * <ul>
 * <li>Create a new room (POST /rooms)</li>
 * <li>Retrieve a paginated list of rooms (GET /rooms)</li>
 * <li>Retrieve a single room by id (GET /rooms/{id})</li>
 * <li>Update an existing room (PUT /rooms/{id})</li>
 * </ul>
 * </p>
 *
 * <p>
 * Input validation is performed on request DTOs; invalid requests return
 * appropriate HTTP error responses with validation details.
 * </p>
 *
 * @see RoomService
 * @see RoomRequestDTO
 * @see RoomResponseDTO
 * @since 1.0.0
 */
@RestController
@RequestMapping("/rooms")
@Validated
@RequiredArgsConstructor
public class RoomController {
  private final RoomService roomService;

  /**
   * Create a new room.
   *
   * @param roomRequestDTO the room payload to create
   * @return the created room as a response DTO
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RoomResponseDTO createRoom(@Valid @RequestBody RoomRequestDTO roomRequestDTO) {
    return roomService.createRoom(roomRequestDTO);
  }

  /**
   * Retrieve a paginated list of rooms.
   *
   * @param pageable pagination and sorting information
   * @return a page of room response DTOs
   */
  @GetMapping
  public Page<RoomResponseDTO> getAllRooms(Pageable pageable) {
    return roomService.getAllRooms(pageable);
  }

  /**
   * Retrieve a single room by its identifier.
   *
   * @param id the room id (must be >= 1)
   * @return the room response DTO
   */
  @GetMapping("/{id}")
  public RoomResponseDTO getRoomById(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    return roomService.getRoomById(id);
  }

  /**
   * Update an existing room.
   *
   * @param id             the id of the room to update
   * @param roomRequestDTO the updated room payload
   * @return the updated room as a response DTO
   */
  @PutMapping("/{id}")
  public RoomResponseDTO updateRoom(
      @PathVariable @Min(value = 1, message = "{id.min}") long id,
      @Valid @RequestBody RoomRequestDTO roomRequestDTO) {
    return roomService.updateRoom(id, roomRequestDTO);
  }

  /**
   * Deletes a room by its unique identifier.
   *
   * @param id the ID of the room to delete; must be greater than 0
   * @throws ResourceNotFoundException if no room with the specified ID exists
   *
   * @see RoomService#deleteRoomById(long)
   */
  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteRoomById(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    roomService.deleteRoomById(id);
  }
}
