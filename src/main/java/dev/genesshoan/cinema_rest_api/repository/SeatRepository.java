package dev.genesshoan.cinema_rest_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.genesshoan.cinema_rest_api.entity.Seat;
import jakarta.persistence.LockModeType;

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

  /**
   * Counts the number of available seats matching the given IDs.
   * 
   * <p>
   * This method is useful for quickly validating seat availability
   * before attempting to reserve or purchase tickets.
   * </p>
   * 
   * @param ids List of seat IDs to check
   * @return The count of seats that exist and have AVAILABLE status
   */
  @Query("""
          SELECT COUNT(s)
          FROM Seat s
          WHERE s.id IN :ids
            AND s.status = 'AVAILABLE'
      """)
  long countAvailableByIds(@Param("ids") List<Long> ids);

  /**
   * Retrieves available seats by IDs with a pessimistic write lock.
   * 
   * <p>
   * This method acquires a database-level write lock on the matching seats
   * to prevent concurrent modifications during ticket purchase transactions.
   * The lock is held until the transaction commits or rolls back, ensuring
   * that no other transaction can modify or lock these seats simultaneously.
   * </p>
   * 
   * <p>
   * Use this method within a transactional context when you need to:
   * <ul>
   * <li>Reserve seats for ticket purchase</li>
   * <li>Prevent race conditions in concurrent booking scenarios</li>
   * <li>Ensure seat availability doesn't change between check and update</li>
   * </ul>
   * </p>
   * 
   * @param ids List of seat IDs to retrieve and lock
   * @return List of available seats matching the given IDs, sorted by seat ID.
   *         Returns empty list if no seats match or all are unavailable.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
          SELECT s
          FROM Seat s
          WHERE s.id IN :ids
            AND s.status = 'AVAILABLE'
          ORDER BY s.id
      """)
  List<Seat> findAvailableByIdsForUpdate(@Param("ids") List<Long> ids);
}
