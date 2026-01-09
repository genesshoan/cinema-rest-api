package dev.genesshoan.cinema_rest_api.exception;

/**
 * Thrown when a request is syntactically valid but fails business or domain
 * validation performed by the service layer.
 *
 * Use this exception to indicate that the client provided an input that the
 * server cannot accept due to application rules (missing required business
 * data, invalid ranges, etc.). This maps to HTTP 400 (Bad Request) by
 * convention in the API exception handlers.
 */
public class InvalidRequestException extends RuntimeException {
  /**
   * Create a new InvalidRequestException with a descriptive message.
   *
   * @param message a human-readable description of the validation problem
   */
  public InvalidRequestException(String message) {
    super(message);
  }
}
