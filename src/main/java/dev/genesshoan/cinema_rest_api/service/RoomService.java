package dev.genesshoan.cinema_rest_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.genesshoan.cinema_rest_api.dto.RoomRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.RoomResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.RoomMapper;
import dev.genesshoan.cinema_rest_api.repository.RoomRepository;

@Service
public class RoomService {
  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;

  public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
    this.roomRepository = roomRepository;
    this.roomMapper = roomMapper;
  }

  public RoomResponseDTO createRoom(RoomRequestDTO roomRequestDTO) {
    Room room = roomMapper.toEntity(roomRequestDTO);

    if (roomRepository.existsByName(room.getName())) {
      throw new ResourceAlreadyExistsException("Room with name " + room.getName() + " already exists");
    }

    return roomMapper.toDto(
        roomRepository.save(room));
  }

  public Page<RoomResponseDTO> getAllRooms(Pageable pageable) {
    return roomRepository.findAll(pageable).map(roomMapper::toDto);
  }

  public RoomResponseDTO getRoomById(Long id) {
    Room room = roomRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " was not found"));

    return roomMapper.toDto(room);
  }

  public RoomResponseDTO updateRoom(Long id, RoomRequestDTO roomRequestDTO) {
    Room existing = roomRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Room with id " + id + " was not found"));

    existing.setName(roomRequestDTO.name());
    existing.setRows(roomRequestDTO.rows());
    existing.setSeatsPerRow(roomRequestDTO.seatsPerRow());

    return roomMapper.toDto(
        roomRepository.save(existing));
  }

  // TODO: implemente public deleteById(Long id)
}
