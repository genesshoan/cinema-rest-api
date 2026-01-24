package dev.genesshoan.cinema_rest_api.dto.ticket;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for ticket information in API responses.
 * 
 * <p>
 * This immutable record represents a purchased ticket with details about
 * the customer, movie, seat location, and showtime. It is used when
 * returning ticket information to clients after a successful purchase.
 * </p>
 * 
 * <p>
 * Contains aggregated information from multiple entities (Ticket, Seat,
 * Showtime, Movie) to provide a complete ticket view without exposing
 * internal entity relationships.
 * </p>
 * 
 * @param owner          the name of the customer who purchased the ticket
 * @param title          the movie title for this ticket
 * @param rowNumber      the seat row number in the cinema room
 * @param seatNumber     the seat number within the row
 * @param purcharseDate  the date and time when the ticket was purchased
 * @param showDateTime   the date and time of the movie showtime
 *
 * @see TicketSaleResponseDTO
 * @since 1.0.0
 */
public record TicketResponseDTO(
    String owner,
    String title,
    Integer rowNumber,
    Integer seatNumber,
    LocalDateTime purcharseDate,
    LocalDateTime showDateTime) {
}
