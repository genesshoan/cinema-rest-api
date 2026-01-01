package dev.genesshoan.cinema_rest_api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handler for persistence / database related exceptions.
 */
@ControllerAdvice
@Order(3)
public class PersistenceExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(PersistenceExceptionHandler.class);

  /**
   * Handle DataIntegrityViolationException and return HTTP 409.
   *
   * Why this can occur:
   * - Violations of database constraints (unique, not null, foreign key).
   * - Attempts to persist inconsistent or invalid state.
   * - Concurrent updates causing constraint collisions.
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
      DataIntegrityViolationException ex,
      HttpServletRequest request) {
    log.warn("Data integrity violation: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.CONFLICT,
        "Data integrity violation",
        "The request violates database constraints",
        null,
        request);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
  }

}
