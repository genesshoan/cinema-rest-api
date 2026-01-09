package dev.genesshoan.cinema_rest_api.exception;

/**
 * Thrown when a requested showtime overlaps with existing showtimes in the
 * same room.
 *
 * This represents a domain-level conflict (business rule violation). The API
 * maps this to HTTP 422 (Unprocessable Entity) via the global exception
 * handler to indicate the request could not be processed because of domain
 * constraints.
 */
public class OverlapingShowtimesException extends RuntimeException {
  /**
   * Create a new OverlapingShowtimesException with a descriptive message.
   *
   * @param message explanation of the overlap condition
   */
  public OverlapingShowtimesException(String message) {
    super(message);
  }
}
