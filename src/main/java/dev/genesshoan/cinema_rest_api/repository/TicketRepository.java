package dev.genesshoan.cinema_rest_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.genesshoan.cinema_rest_api.entity.Ticket;
import jakarta.persistence.LockModeType;

/**
 * Repository interface for {@link Ticket} entity persistence operations.
 * 
 * <p>
 * Provides data access methods for ticket management including standard
 * CRUD operations inherited from {@link JpaRepository}.
 * </p>
 * 
 * @see Ticket
 * @since 1.0.0
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
  /**
   * Retrieves a ticket by ID with a pessimistic write lock and eagerly fetches the seat association.
   * 
   * <p>
   * This method acquires a database-level write lock on the ticket entity
   * to prevent concurrent modifications during ticket update operations (e.g.,
   * cancellation, status changes). The lock is held until the transaction
   * commits or rolls back.
   * </p>
   * 
   * <p>
   * The seat association is eagerly loaded using JOIN FETCH to avoid N+1
   * queries and ensure the seat data is available within the same transaction.
   * </p>
   * 
   * <p>
   * Use this method when you need to:
   * <ul>
   * <li>Update ticket status (ACTIVE → CANCELLED or CONSUMED)</li>
   * <li>Prevent concurrent modifications to the same ticket</li>
   * <li>Access seat information along with the ticket</li>
   * </ul>
   * </p>
   * 
   * @param id The ticket ID to retrieve and lock
   * @return Optional containing the ticket if found, empty otherwise
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("""
            SELECT t
            FROM Ticket t
            JOIN FETCH t.seat
            WHERE t.id = :id
      """)
  Optional<Ticket> findByIdForUpdate(@Param("id") Long id);
}
