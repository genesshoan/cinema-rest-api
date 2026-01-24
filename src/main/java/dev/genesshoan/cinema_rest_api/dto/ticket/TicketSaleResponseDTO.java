package dev.genesshoan.cinema_rest_api.dto.ticket;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object for ticket sale confirmation responses.
 * 
 * <p>
 * This immutable record represents the result of a successful ticket purchase
 * transaction. It provides a summary of the purchase (total price and ticket
 * count) along with detailed information for each individual ticket.
 * </p>
 * 
 * <p>
 * Use this DTO to:
 * <ul>
 * <li>Confirm successful ticket purchases to customers</li>
 * <li>Provide receipt information for the transaction</li>
 * <li>Display booking confirmation details in the UI</li>
 * </ul>
 * </p>
 * 
 * @param totalPrice   the total price for all purchased tickets
 * @param totalTickets the number of tickets purchased in this transaction
 * @param tickets      list of individual ticket details for each purchased seat
 *
 * @see TicketResponseDTO
 * @see TicketSaleRequestDTO
 * @since 1.0.0
 */
public record TicketSaleResponseDTO(
    BigDecimal totalPrice,
    Integer totalTickets,
    List<TicketResponseDTO> tickets) {
}
