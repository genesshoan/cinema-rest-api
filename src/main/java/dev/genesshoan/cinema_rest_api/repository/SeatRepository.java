package dev.genesshoan.cinema_rest_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.genesshoan.cinema_rest_api.entity.Seat;

/**
 * Repository interface for {@link Seat} entity persistence operations.
 * 
 * <p>
 * Provides data access methods for seat management including
 * retrieval of seat maps for showtimes.
 * </p>
 * 
 * @see Seat
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
  /**
   * Finds all seats for a given showtime ordered by row and seat number.
   * 
   * @param showtimeId The ID of the showtime
   * @return List of seats sorted by row number (ascending) and seat number
   *         (ascending)
   */
  List<Seat> findByIdOrderByRowNumberAscSeatNumberAsc(Long showtimeId);
}
