package dev.genesshoan.cinema_rest_api.mapper;

import org.springframework.stereotype.Component;

import dev.genesshoan.cinema_rest_api.dto.ticket.TicketResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Ticket;

/**
 * Mapper component for converting between Ticket entities and DTOs.
 * 
 * <p>
 * This mapper handles the transformation between {@link Ticket} entities
 * and their corresponding Data Transfer Objects ({@link TicketResponseDTO}).
 * </p>
 * 
 * <p>
 * Mapping rules:
 * <ul>
 * <li>Entity to DTO: includes customer name, movie title, seat information, and
 * dates</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Note: these methods expect non-null inputs. If a null input is provided the
 * method will result in a {@link NullPointerException} at runtime.
 * Callers should validate inputs before invoking the mapper or handle the
 * exception appropriately.
 * </p>
 * 
 * <p>
 * The {@code toDto} method assumes the seat and showtime associations are
 * initialized.
 * Ensure they are loaded in a transactional context before calling this method.
 * </p>
 * 
 * @since 1.0.0
 */
@Component
public class TicketMapper {
  /**
   * Converts a Ticket entity to a TicketResponseDTO.
   * 
   * Maps the ticket's customer name, associated movie title from the showtime,
   * seat row and number, purchase date, and show date/time.
   * 
   * @param ticket the ticket entity to convert (must be non-null)
   * @return the corresponding response DTO
   * @throws NullPointerException if ticket is null or if its seat/showtime/movie
   *                              associations are not initialized
   */
  public TicketResponseDTO toDto(Ticket ticket) {
    return new TicketResponseDTO(
        ticket.getCustomerName(),
        ticket.getSeat().getShowtime().getMovie().getTitle(),
        ticket.getSeat().getRowNumber(),
        ticket.getSeat().getSeatNumber(),
        ticket.getPurcharse(),
        ticket.getSeat().getShowtime().getStartTime());
  }
}
