package dev.genesshoan.cinema_rest_api.mapper;

import org.springframework.stereotype.Component;

import dev.genesshoan.cinema_rest_api.dto.room.RoomRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.room.RoomResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Room;

/**
 * Mapper responsible for converting between Room entity and its DTO representations.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Convert a {@link Room} entity to {@link RoomResponseDTO} for API responses.</li>
 *   <li>Convert a {@link RoomRequestDTO} to a {@link Room} entity for persistence.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Note: these methods expect non-null inputs. If a null input is provided the
 * method will result in a {@link NullPointerException} at runtime.
 * Callers should validate inputs before invoking the mapper or handle the
 * exception appropriately.
 * </p>
 */
@Component
public class RoomMapper {
  /**
   * Convert a {@link Room} entity into a {@link RoomResponseDTO}.
   *
   * @param room the entity to convert (must be non-null)
   * @return a RoomResponseDTO containing the entity's id, name, rows and seatsPerRow
   * @throws NullPointerException if {@code room} is null
   */
  public RoomResponseDTO toDto(Room room) {
    return new RoomResponseDTO(
        room.getId(),
        room.getName(),
        room.getRows(),
        room.getSeatsPerRow());
  }

  /**
   * Create a new {@link Room} entity from the provided {@link RoomRequestDTO}.
   *
   * <p>The returned entity is a fresh instance with fields copied from the
   * request DTO. It does not set persistence-related fields like {@code id}.
   * </p>
   *
   * @param roomRequestDTO the DTO containing room data (must be non-null)
   * @return a new Room entity populated from the request DTO
   * @throws NullPointerException if {@code roomRequestDTO} is null
   */
  public Room toEntity(RoomRequestDTO roomRequestDTO) {
    Room room = new Room();
    room.setName(roomRequestDTO.name());
    room.setRows(roomRequestDTO.rows());
    room.setSeatsPerRow(roomRequestDTO.seatsPerRow());

    return room;
  }
}
