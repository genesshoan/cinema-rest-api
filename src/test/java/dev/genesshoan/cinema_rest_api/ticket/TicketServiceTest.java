package dev.genesshoan.cinema_rest_api.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.genesshoan.cinema_rest_api.dto.ticket.TicketResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Seat;
import dev.genesshoan.cinema_rest_api.entity.SeatStatus;
import dev.genesshoan.cinema_rest_api.entity.Showtime;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.entity.Ticket;
import dev.genesshoan.cinema_rest_api.entity.TicketStatus;
import dev.genesshoan.cinema_rest_api.exception.IllegalStatusException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.TicketMapper;
import dev.genesshoan.cinema_rest_api.repository.SeatRepository;
import dev.genesshoan.cinema_rest_api.repository.ShowtimeRepository;
import dev.genesshoan.cinema_rest_api.repository.TicketRepository;
import dev.genesshoan.cinema_rest_api.service.TicketService;

/**
 * Unit tests for {@link TicketService}.
 */
@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
  @Mock
  private TicketRepository ticketRepository;

  @Mock
  private ShowtimeRepository showtimeRepository;

  @Mock
  private SeatRepository seatRepository;

  @Mock
  private TicketMapper ticketMapper;

  @InjectMocks
  private TicketService ticketService;

  private Ticket ticket;
  private Seat seat;
  private Pageable pageable;
  private TicketResponseDTO ticketResponseDTO;

  @BeforeEach
  void setUp() {
    Showtime showtime = new Showtime();
    showtime.setId(10L);
    showtime.setStartTime(LocalDateTime.now().plusDays(1));
    showtime.setStatus(ShowtimeStatus.SCHEDULED);

    seat = new Seat();
    seat.setId(20L);
    seat.setRowNumber(2);
    seat.setSeatNumber(6);
    seat.setStatus(SeatStatus.SOLD);
    seat.setShowtime(showtime);

    ticket = new Ticket();
    ticket.setId(30L);
    ticket.setCustomerName("John Doe");
    ticket.setPrice(new BigDecimal("12.50"));
    ticket.setStatus(TicketStatus.ACTIVE);
    ticket.setSeat(seat);
    ticket.setPurcharse(LocalDateTime.now());

    pageable = PageRequest.of(0, 10);

    ticketResponseDTO = new TicketResponseDTO(
        "John Doe",
        "Before Sunrise",
        2,
        6,
        ticket.getPurcharse(),
        showtime.getStartTime());
  }

  /**
   * Verifies that listing tickets maps repository page results to DTOs.
   */
  @Test
  @DisplayName("listTickets - should return paged DTOs")
  void listTickets_ShouldReturnPagedDtos() {
    Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket), pageable, 1);
    when(ticketRepository.search(10L, TicketStatus.ACTIVE, pageable)).thenReturn(ticketPage);
    when(ticketMapper.toDto(ticket)).thenReturn(ticketResponseDTO);

    Page<TicketResponseDTO> result = ticketService.listTickets(10L, TicketStatus.ACTIVE, pageable);

    assertThat(result.getTotalElements()).isEqualTo(1);
    assertThat(result.getContent()).containsExactly(ticketResponseDTO);
    verify(ticketRepository).search(10L, TicketStatus.ACTIVE, pageable);
  }

  /**
   * Verifies that getting a ticket by id returns mapped DTO details.
   */
  @Test
  @DisplayName("getTicketById - existing id: should return DTO")
  void getTicketById_WhenExists_ShouldReturnDto() {
    when(ticketRepository.findByIdWithDetails(30L)).thenReturn(Optional.of(ticket));
    when(ticketMapper.toDto(ticket)).thenReturn(ticketResponseDTO);

    TicketResponseDTO result = ticketService.getTicketById(30L);

    assertThat(result).isEqualTo(ticketResponseDTO);
    verify(ticketRepository).findByIdWithDetails(30L);
  }

  /**
   * Verifies that a missing ticket id throws ResourceNotFoundException.
   */
  @Test
  @DisplayName("getTicketById - missing id: should throw ResourceNotFoundException")
  void getTicketById_WhenMissing_ShouldThrow() {
    when(ticketRepository.findByIdWithDetails(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> ticketService.getTicketById(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Ticket with id 999does not exist");
  }

  /**
   * Verifies that confirmTicket consumes an active ticket.
   */
  @Test
  @DisplayName("confirmTicket - active ticket: should set status CONSUMED")
  void confirmTicket_WhenActive_ShouldConsume() {
    when(ticketRepository.findByIdForUpdate(30L)).thenReturn(Optional.of(ticket));

    ticketService.confirmTicket(30L);

    assertThat(ticket.getStatus()).isEqualTo(TicketStatus.CONSUMED);
    verify(ticketRepository).findByIdForUpdate(30L);
  }

  /**
   * Verifies that non-active tickets cannot be confirmed.
   */
  @Test
  @DisplayName("confirmTicket - non active ticket: should throw IllegalStatusException")
  void confirmTicket_WhenNotActive_ShouldThrow() {
    ticket.setStatus(TicketStatus.CANCELLED);
    when(ticketRepository.findByIdForUpdate(30L)).thenReturn(Optional.of(ticket));

    assertThatThrownBy(() -> ticketService.confirmTicket(30L))
        .isInstanceOf(IllegalStatusException.class)
        .hasMessageContaining("A non active ticket cannot be consumed");
  }

  /**
   * Verifies hasActiveTicketsByShowtimeId delegates to repository status query.
   */
  @Test
  @DisplayName("hasActiveTicketsByShowtimeId - should delegate to repository")
  void hasActiveTicketsByShowtimeId_ShouldDelegateToRepository() {
    when(ticketRepository.existsBySeatShowtimeIdAndStatus(10L, TicketStatus.ACTIVE)).thenReturn(true);

    boolean result = ticketService.hasActiveTicketsByShowtimeId(10L);

    assertThat(result).isTrue();
    verify(ticketRepository).existsBySeatShowtimeIdAndStatus(10L, TicketStatus.ACTIVE);
  }
}
