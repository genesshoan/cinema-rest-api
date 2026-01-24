package dev.genesshoan.cinema_rest_api.dto.ticket;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for ticket purchase requests.
 * 
 * <p>
 * This immutable record encapsulates all required information for purchasing
 * one or more tickets for a specific showtime. All fields are validated using
 * Bean Validation annotations to ensure data integrity.
 * </p>
 * 
 * <p>
 * Validation rules:
 * <ul>
 * <li>Showtime ID: required, must be greater than 0</li>
 * <li>Seat IDs: required, must contain at least one seat ID</li>
 * <li>Customer name: required, 1-255 characters</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Business rules enforced by the service layer:
 * <ul>
 * <li>All seats must belong to the specified showtime</li>
 * <li>All seats must have AVAILABLE status</li>
 * <li>Duplicate seat IDs in the list are not allowed</li>
 * </ul>
 * </p>
 * 
 * @param showtimeId   the ID of the showtime for which tickets are being purchased
 * @param seatIds      list of seat IDs to reserve and purchase
 * @param customerName the name of the customer purchasing the tickets
 *
 * @see TicketSaleResponseDTO
 * @since 1.0.0
 */
public record TicketSaleRequestDTO(
    @NotNull(message = "{ticket.showtime.required}") @Min(value = 1, message = "{id.min}") Long showtimeId,
    @NotNull(message = "{ticket.seats.required}") @Size(min = 1, message = "{ticket.seats.min}") List<Long> seatIds,
    @NotBlank(message = "{ticket.customer-name.required}") @Size(max = 255, message = "{ticket.customer-name.size}") String customerName) {
}
