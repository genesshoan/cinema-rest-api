package dev.genesshoan.cinema_rest_api.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
  /**
   * Check if a scheduled showtime exists that overlaps the provided time
   * interval for the given room.
   *
   * Note: The JPQL uses parameters for start and end times; the implementation
   * expects the caller to pass start/end correctly. Returns true when at
   * least one overlapping scheduled showtime exists.
   */
  @Query("""
        SELECT COUNT(s) > 0
        FROM Showtime s
        WHERE s.room.id = :room_id
          AND s.startTime > :end_time
          AND s.endTime < :start_time
          AND s.status != :status
      """)
  public boolean existsOverlappingShowtime(
      @Param("room_id") Long roomId,
      @Param("start_time") LocalDateTime startTime,
      @Param("end_time") LocalDateTime endTime,
      @Param("status") ShowtimeStatus status);

  /**
   * Search for showtimes within an optional time window and optional filters
   * for room, movie and status. Returns a paginated result.
   */
  @Query("""
        SELECT s
        FROM Showtime s
        WHERE (:from IS NULL OR s.startTime >= :from)
          AND (:to IS NULL OR s.endTime <= :to)
          AND (:room_id IS NULL OR s.room.id = :room_id)
          AND (:movie_id IS NULL OR s.movie.id = :movie_id)
          AND (:status IS NULL OR s.status = :status)
      """)
  public Page<Showtime> search(
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      @Param("room_id") Long roomId,
      @Param("movie_id") Long movieId,
      @Param("status") ShowtimeStatus status,
      Pageable pageable);
}
