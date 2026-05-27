package dev.genesshoan.cinema_rest_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.genesshoan.cinema_rest_api.dto.ticket.TicketResponseDTO;
import dev.genesshoan.cinema_rest_api.dto.ticket.TicketSaleRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.ticket.TicketSaleResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.TicketStatus;
import dev.genesshoan.cinema_rest_api.service.TicketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for ticket booking and lifecycle operations.
 */
@RestController
@RequestMapping("/api/v1/tickets")
@Validated
@RequiredArgsConstructor
public class TicketController {
  private final TicketService ticketService;

  /**
   * Books one or more tickets for a showtime.
   *
   * @param requestDTO booking request payload
   * @return sale summary with generated tickets
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TicketSaleResponseDTO bookTickets(@Valid @RequestBody TicketSaleRequestDTO requestDTO) {
    return ticketService.sellTicket(requestDTO);
  }

  /**
   * Lists tickets with optional filtering.
   *
   * @param showtimeId optional showtime filter
   * @param status     optional status filter
   * @param pageable   pagination information
   * @return a page of ticket DTOs
   */
  @GetMapping
  public Page<TicketResponseDTO> listTickets(
      @RequestParam(required = false) @Min(value = 1, message = "{id.min}") Long showtimeId,
      @RequestParam(required = false) TicketStatus status,
      Pageable pageable) {
    return ticketService.listTickets(showtimeId, status, pageable);
  }

  /**
   * Retrieves ticket details by id.
   *
   * @param id ticket identifier
   * @return ticket details
   */
  @GetMapping("/{id}")
  public TicketResponseDTO getTicketById(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    return ticketService.getTicketById(id);
  }

  /**
   * Confirms a booking.
   *
   * @param id ticket identifier
   */
  @PutMapping("/{id}/confirm")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void confirmTicket(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    ticketService.confirmTicket(id);
  }

  /**
   * Cancels a booking.
   *
   * @param id ticket identifier
   */
  @PutMapping("/{id}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancelTicket(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    ticketService.cancelTicket(id);
  }
}
