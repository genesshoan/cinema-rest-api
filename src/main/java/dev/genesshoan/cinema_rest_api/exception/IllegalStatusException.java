package dev.genesshoan.cinema_rest_api.exception;

/**
 * Thrown when an operation is attempted on an entity that is in an invalid or
 * inappropriate status for that operation.
 *
 * <p>
 * This exception indicates that while the entity exists and the request is
 * syntactically valid, the entity's current status does not allow the
 * requested operation to proceed.
 * </p>
 *
 * <p>
 * Common scenarios include:
 * <ul>
 *   <li>Attempting to sell tickets for a cancelled or completed showtime</li>
 *   <li>Trying to use or modify a cancelled ticket</li>
 *   <li>Performing operations on resources that are no longer in an active state</li>
 * </ul>
 * </p>
 *
 * <p>
 * This maps to HTTP 422 (Unprocessable Entity) by convention in the API
 * exception handlers, as the server understands the request but cannot process
 * it due to the entity's state.
 * </p>
 */
public class IllegalStatusException extends RuntimeException {
  /**
   * Create a new IllegalStatusException with a descriptive message.
   *
   * @param message a human-readable description of why the operation cannot
   *                be performed due to the entity's status
   */
  public IllegalStatusException(String message) {
    super(message);
  }
}
