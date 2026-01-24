package dev.genesshoan.cinema_rest_api.exception;

/**
 * Thrown when attempting to sell or reserve a seat that is not in AVAILABLE status.
 *
 * <p>
 * This exception is raised during ticket purchase operations when one or more
 * requested seats are already sold, reserved, or otherwise unavailable. It
 * represents a business rule violation that prevents completing the transaction.
 * </p>
 *
 * <p>
 * Common scenarios include:
 * <ul>
 * <li>Seat has already been sold to another customer</li>
 * <li>Seat is in a reserved state (temporarily held)</li>
 * <li>Concurrent purchase attempts for the same seat</li>
 * <li>Seat ID does not exist or belongs to a different showtime</li>
 * </ul>
 * </p>
 *
 * <p>
 * The API maps this exception to HTTP 409 (Conflict) to indicate that the
 * request conflicts with the current state of the resource.
 * </p>
 *
 * @see dev.genesshoan.cinema_rest_api.entity.Seat
 * @see dev.genesshoan.cinema_rest_api.entity.SeatStatus
 * @since 1.0.0
 */
public class SeatNotAvailableException extends RuntimeException {
  /**
   * Creates a new SeatNotAvailableException with a descriptive message.
   *
   * @param message a human-readable explanation of why the seat(s) are not available
   */
  public SeatNotAvailableException(String message) {
    super(message);
  }
}
