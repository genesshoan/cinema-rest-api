package dev.genesshoan.cinema_rest_api.error;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(3)
public class PersistenceExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(
      DataIntegrityViolationException ex,
      HttpServletRequest request) {
    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.CONFLICT,
        "Data integrity violation",
        "The request violates database constraints",
        null,
        request);

    return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
  }

}
