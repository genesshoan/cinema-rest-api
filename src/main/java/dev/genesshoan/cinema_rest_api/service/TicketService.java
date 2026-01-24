package dev.genesshoan.cinema_rest_api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.genesshoan.cinema_rest_api.dto.ticket.TicketResponseDTO;
import dev.genesshoan.cinema_rest_api.dto.ticket.TicketSaleRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.ticket.TicketSaleResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Seat;
import dev.genesshoan.cinema_rest_api.entity.SeatStatus;
import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.entity.Ticket;
import dev.genesshoan.cinema_rest_api.entity.TicketStatus;
import dev.genesshoan.cinema_rest_api.exception.IllegalStatusException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.exception.SeatNotAvailableException;
import dev.genesshoan.cinema_rest_api.mapper.TicketMapper;
import dev.genesshoan.cinema_rest_api.repository.SeatRepository;
import dev.genesshoan.cinema_rest_api.repository.ShowtimeRepository;
import dev.genesshoan.cinema_rest_api.repository.TicketRepository;
import lombok.RequiredArgsConstructor;

/**
 * Ticket domain service.
 *
 * <p>
 * Coordinates ticket sales, lookups, and lifecycle operations with
 * transactional
 * guarantees. Enforces seat availability and uses pessimistic locking to
 * prevent
 * race conditions during purchase and status transitions.
 * </p>
 *
 * <p>
 * Responsibilities:
 * <ul>
 * <li>sellTicket: validates showtime and seats, creates tickets, updates seat
 * status, and returns a sale summary</li>
 * <li>getTicketById: retrieves a single ticket and maps it to a DTO</li>
 * <li>cancelTicket: marks a ticket as CANCELLED and restores the associated
 * seat to AVAILABLE</li>
 * <li>consumeTicket: marks a ticket as CONSUMED for entry validation</li>
 * </ul>
 * </p>
 *
 * <p>
 * Error modes:
 * <ul>
 * <li>ResourceNotFoundException: showtime or ticket not found</li>
 * <li>SeatNotAvailableException: one or more requested seats are not
 * available</li>
 * </ul>
 * </p>
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketService {
  private final TicketRepository ticketRepository;
  private final ShowtimeRepository showtimeRepository;
  private final SeatRepository seatRepository;
  private final TicketMapper ticketMapper;

  /**
   * Processes a ticket sale transaction for one or more seats.
   *
   * <p>
   * This method handles the complete ticket purchase workflow:
   * </p>
   * <ol>
   * <li>Validates that the showtime exists</li>
   * <li>Acquires pessimistic locks on requested seats and verifies
   * availability</li>
   * <li>Creates ticket records for each seat</li>
   * <li>Updates seat status to SOLD</li>
   * <li>Calculates total price based on showtime base price</li>
   * <li>Returns purchase confirmation with ticket details</li>
   * </ol>
   *
   * <p>
   * The entire operation runs within a single database transaction with
   * pessimistic write locks on seats to prevent race conditions. If any step
   * fails, all changes are rolled back.
   * </p>
   *
   * @param requestDTO the ticket sale request containing showtime ID, seat IDs,
   *                   and customer name
   * @return a response containing total price, ticket count, and individual
   *         ticket details
   * @throws ResourceNotFoundException if the showtime does not exist
   * @throws SeatNotAvailableException if any of the requested seats are not
   *                                   available or do not exist
   * @see TicketSaleRequestDTO
   * @see TicketSaleResponseDTO
   */
  @Transactional
  public TicketSaleResponseDTO sellTicket(TicketSaleRequestDTO requestDTO) {
    Showtime showtime = showtimeRepository.findById(requestDTO.showtimeId())
        .orElseThrow(
            () -> new ResourceNotFoundException("Showtime with id " + requestDTO.showtimeId() + " does not exist"));

    List<Seat> seats = seatRepository.findAvailableByIdsForUpdate(requestDTO.seatIds());

    if (seats.size() != requestDTO.seatIds().size()) {
      throw new SeatNotAvailableException("At least one selected seat is not available");
    }

    List<Ticket> tickets = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;

    for (Seat seat : seats) {
      seat.setStatus(SeatStatus.SOLD);

      Ticket ticket = new Ticket();

      ticket.setCustomerName(requestDTO.customerName());
      ticket.setPrice(showtime.getBasePrice());
      ticket.setSeat(seat);
      ticket.setStatus(TicketStatus.ACTIVE);

      tickets.add(ticket);
      totalPrice = totalPrice.add(ticket.getPrice());
    }

    ticketRepository.saveAll(tickets);

    return new TicketSaleResponseDTO(
        totalPrice,
        tickets.size(),
        tickets.stream()
            .map(ticketMapper::toDto)
            .collect(Collectors.toList()));
  }

  // TODO: refactor mapper to avoid deep lazy traversal (N+1 risk)

  /**
   * Retrieves a ticket by its identifier and maps it to a DTO.
   *
   * @param id the ticket identifier
   * @return a DTO representation of the ticket
   * @throws ResourceNotFoundException if the ticket does not exist
   */
  public TicketResponseDTO getTicketById(long id) {
    Ticket ticket = ticketRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + "does not exist"));

    return ticketMapper.toDto(ticket);
  }

  /**
   * Cancels an existing ticket.
   *
   * <p>
   * Transitions ticket status to {@code CANCELLED} and restores the associated
   * seat to {@code AVAILABLE}. Executed within a write transaction with a row
   * lock
   * to ensure consistency.
   * </p>
   *
   * @param id the ticket identifier
   * @throws ResourceNotFoundException if the ticket does not exist
   * @throws IllegalStatusException    if the ticket is not active
   */
  @Transactional
  public void cancelTicket(long id) {
    Ticket ticket = ticketRepository.findByIdForUpdate(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + "does not exist"));

    if (ticket.getStatus() != TicketStatus.ACTIVE) {
      throw new IllegalStatusException("A non active ticket cannot be cancelled");
    }

    ticket.setStatus(TicketStatus.CANCELLED);
    ticket.getSeat().setStatus(SeatStatus.AVAILABLE);
  }

  /**
   * Consumes a ticket for entry validation.
   *
   * <p>
   * Transitions the ticket status to {@code CONSUMED}. This operation does not
   * alter seat status and should be used when a customer enters the room.
   * </p>
   *
   * @param id the ticket identifier
   * @throws ResourceNotFoundException if the ticket does not exist
   * @throws IllegalStatusException    if the ticket is not active
   */
  @Transactional
  public void consumeTicket(long id) {
    Ticket ticket = ticketRepository.findByIdForUpdate(id)
        .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + "does not exist"));

    if (ticket.getStatus() != TicketStatus.ACTIVE) {
      throw new IllegalStatusException("A non active ticket cannot be consumed");
    }

    ticket.setStatus(TicketStatus.CONSUMED);
  }
}
