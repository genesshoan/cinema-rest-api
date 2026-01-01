package dev.genesshoan.cinema_rest_api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Exception handlers related to domain/resource operations.
 *
 * These handlers translate domain-specific exceptions (resource not found,
 * resource already exists) into appropriate HTTP ProblemDetail responses.
 */
@ControllerAdvice
@Order(4)
public class DomainExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(DomainExceptionHandler.class);

  /**
   * Handle cases where a requested resource cannot be found.
   *
   * Why this can occur:
   * - The client requested an entity by id that does not exist in the database.
   * - The resource has been deleted or never existed.
   * - The query criteria do not match any persisted entity.
   *
   * Returns HTTP 404 (Not Found).
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(
      ResourceNotFoundException ex,
      HttpServletRequest request) {
    log.info("Resource not found: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.NOT_FOUND, "Resource not found",
        ex.getMessage(),
        null,
        request);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  /**
   * Handle attempts to create a resource that already exists.
   *
   * Why this can occur:
   * - The client attempts to create an entity that violates a uniqueness
   *   constraint (for example, duplicate natural key or unique field).
   * - Duplicate requests or retries that should be de-duplicated by the client.
   *
   * Returns HTTP 409 (Conflict).
   */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ProblemDetail> handleResourceAlreadyExists(
      ResourceAlreadyExistsException ex,
      HttpServletRequest request) {
    log.warn("Resource already exists: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.CONFLICT,
        "Resource already exists",
        ex.getMessage(),
        null,
        request);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
  }

}
