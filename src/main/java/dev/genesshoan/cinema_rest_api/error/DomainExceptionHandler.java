package dev.genesshoan.cinema_rest_api.error;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(4)
public class DomainExceptionHandler {

  /**
   * Returned when a resource get request conflicts
   * with an existing resource (unique constraint violation).
   *
   * HTTP 409 Conflict
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleResourceNotFound(
      ResourceNotFoundException ex,
      HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.NOT_FOUND, "Resource not found",
        ex.getMessage(),
        null,
        request);

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
  }

  /**
   * Returned when a resource creation request conflicts
   * with an existing resource (unique constraint violation).
   *
   * HTTP 409 Conflict
   */
  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ProblemDetail> handleResourceAlreadyExists(
      ResourceAlreadyExistsException ex,
      HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.CONFLICT,
        "Resource already exists",
        ex.getMessage(),
        null,
        request);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
  }

}
