package dev.genesshoan.cinema_rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  /**
   * Checks if a room has any active (scheduled or not yet completed) showtimes.
   *
   * A room is considered to have active showtimes if it has at least one
   * showtime with status SCHEDULED.
   *
   * @param roomId the ID of the room to check
   * @return {@code true} if the room has active showtimes, {@code false} otherwise
   */
  @Query("""
        SELECT COUNT(s) > 0
        FROM Room r
        JOIN r.showtimes s
        WHERE r.id = :roomId
          AND s.status = 'SCHEDULED'
      """)
  public boolean hasActiveShowtimes(@Param("roomId") Long roomId);
}
