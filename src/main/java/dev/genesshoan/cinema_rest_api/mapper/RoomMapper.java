package dev.genesshoan.cinema_rest_api.mapper;

import org.springframework.stereotype.Component;

import dev.genesshoan.cinema_rest_api.dto.RoomRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.RoomResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;

@Component
public class RoomMapper {
  public RoomResponseDTO toDto(Room room) {
    return new RoomResponseDTO(
        room.getId(),
        room.getName(),
        room.getRows(),
        room.getSeatsPerRow());
  }

  public Room toEntity(RoomRequestDTO roomRequestDTO) {
    Room room = new Room();
    room.setName(roomRequestDTO.name());
    room.setRows(roomRequestDTO.rows());
    room.setSeatsPerRow(roomRequestDTO.seatsPerRow());

    return room;
  }
}
