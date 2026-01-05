package dev.genesshoan.cinema_rest_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

@RestController
@RequestMapping("/rooms")
@Validated
public class RoomController {
  private final RoomService roomService;

  public RoomController(RoomService roomService) {
    this.roomService = roomService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RoomResponseDTO createMovie(@Valid @RequestBody RoomRequestDTO roomRequestDTO) {
    return roomService.createRoom(roomRequestDTO);
  }

  @GetMapping
  public Page<RoomResponseDTO> getAllMovies(Pageable pageable) {
    return roomService.getAllRooms(pageable);
  }

  @GetMapping("/{id}")
  public RoomResponseDTO getRoomById(@PathVariable @Min(value = 1, message = "{id.min}") Long id) {
    return roomService.getRoomById(id);
  }

  @PutMapping("/{id}")
  public RoomResponseDTO updateRoom(
      @PathVariable Long id,
      @Valid @RequestBody RoomRequestDTO roomRequestDTO) {
    return roomService.updateRoom(id, roomRequestDTO);
  }
}
