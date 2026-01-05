package dev.genesshoan.cinema_rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.genesshoan.cinema_rest_api.entity.Room;

/**
 * Spring Data JPA repository for {@link Room} entities.
 * 
 * Provides standard CRUD operations and custom query rooms for
 * room persistence and retrieval.
 * 
 * @see Room
 * @since 1.0.0
 */
public interface RoomRepository extends JpaRepository<Room, Long> {
  /**
   * Checks if a room with the given id exists.
   *
   * The comparision is case-sensitive.
   *
   * @param name the name to check
   * @return {@code true} if exists a movie with the given 'id'
   *         {@code false} otherwise
   */
  public boolean existsByName(String name);
}
