package dev.genesshoan.cinema_rest_api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Global handler for unexpected exceptions that are not handled by other
 * more specific exception handlers.
 *
 * This handler produces a generic 500 Internal Server Error response and
 * logs the full stacktrace for diagnostics. It acts as a safety net for
 * programming errors, unanticipated runtime failures, or uncaught exceptions
 * coming from controllers or service layers.
 */
@ControllerAdvice
@Order(5)
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Value("${spring.profiles.active}")
  private String activeProfile;

  /**
   * Handle unexpected exceptions and return HTTP 500.
   *
   * Why this can occur:
   * - Programming errors (NullPointerException, IndexOutOfBounds, etc.).
   * - Unhandled failures from downstream systems (database, remote APIs).
   * - Unexpected edge cases that weren't validated earlier.
   *
   * The full exception is logged at ERROR level so the team can investigate
   * the root cause. In non-dev profiles the client receives a generic
   * message to avoid leaking internal details.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleUnexpected(
      Exception ex,
      HttpServletRequest request) {

    log.error("Unexpected error at {} {}", request.getMethod(), request.getRequestURI(), ex);

    String detail = "dev".equals(activeProfile) ? ex.getMessage() : "An unexpected error occurred";

    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error",
        detail,
        null,
        request);

    return ResponseEntity.status(500).body(pd);
  }
}
