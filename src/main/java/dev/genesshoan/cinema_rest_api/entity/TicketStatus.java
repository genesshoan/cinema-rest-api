package dev.genesshoan.cinema_rest_api.entity;

/**
 * Enumeration representing the possible states of a cinema ticket.
 *
 * <p>
 * A ticket progresses through various states during its lifecycle, from being
 * active (purchased and valid), to being used (customer attended the showtime),
 * or cancelled (refunded or voided).
 * </p>
 *
 * @see Ticket
 * @since 1.0.0
 */
public enum TicketStatus {
  /**
   * The ticket is active and valid for the showtime.
   * Customer has purchased the ticket but has not yet attended or cancelled.
   */
  ACTIVE,

  /**
   * The ticket has been cancelled.
   * This may occur due to a customer refund request or administrative action.
   */
  CANCELLED,

  /**
   * The ticket has been used.
   * Customer has attended the showtime and the ticket has been validated/scanned.
   */
  CONSUMED
}
