package dev.genesshoan.cinema_rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.genesshoan.cinema_rest_api.entity.Ticket;

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

}
