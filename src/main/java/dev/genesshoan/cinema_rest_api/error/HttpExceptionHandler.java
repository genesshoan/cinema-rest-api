package dev.genesshoan.cinema_rest_api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Handler for HTTP protocol related exceptions (method not allowed, etc.).
 */
@ControllerAdvice
@Order(2)
public class HttpExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(HttpExceptionHandler.class);

  /**
   * Handle HTTP method not supported exceptions and return 405.
   *
   * Why this can occur:
   * - The client used POST/PUT/DELETE on an endpoint that only supports GET.
   * - Incorrect or outdated client documentation causing wrong HTTP verb usage.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpServletRequest request) {
    log.info("Method not allowed: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.METHOD_NOT_ALLOWED,
        "Method not allowed",
        ex.getMessage(),
        null,
        request);

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(pd);
  }
}
