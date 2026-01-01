package dev.genesshoan.cinema_rest_api.error;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(2)
public class HttpExceptionHandler {

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleMethodNotSupported(
      HttpRequestMethodNotSupportedException ex,
      HttpServletRequest request) {
    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.METHOD_NOT_ALLOWED,
        "Method not allowed",
        ex.getMessage(),
        null,
        request);

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(pd);
  }
}
