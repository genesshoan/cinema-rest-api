package dev.genesshoan.cinema_rest_api.error;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
@Order(1)
public class ValidationExceptionHandler {
  /**
   * Thrown when Bean Validation (@Valid) fails on request body DTOs.
   * Typical cases:
   * - Missing required fields
   * - Invalid field formats
   * - Size / pattern violations
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationExeption(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST,
        "Validation failed",
        "One or more fields are invalid",
        ProblemDetailUtils.groupErrors(
            ex.getFieldErrors(),
            FieldError::getField,
            FieldError::getDefaultMessage),
        request);

    return ResponseEntity.badRequest().body(problemDetail);
  }

  /**
   * Triggered when validation fails on method parameters
   * such as @RequestParam, @PathVariable, or @Validated service methods.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolationException(
      ConstraintViolationException ex,
      HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST,
        "Validation failed",
        "Constraint violation",
        ProblemDetailUtils.groupErrors(
            ex.getConstraintViolations(),
            (ConstraintViolation<?> v) -> v.getPropertyPath().toString(),
            ConstraintViolation::getMessage),
        request);

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handlePathVariableTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST,
        "Path variable invalid type",
        String.format(
            "invalid value '%s' for parameter '%s'. Expected type: %s",
            ex.getValue(),
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
        null,
        request);

    return ResponseEntity.badRequest().body(problemDetail);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ProblemDetail> handleMissingRequestParam(
      MissingServletRequestParameterException ex,
      HttpServletRequest request) {
    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST,
        "Missing request parameter",
        String.format("Required parameter '%s' is missing", ex.getParameterName()),
        null,
        request);

    return ResponseEntity.badRequest().body(pd);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpNotReadableException(
      HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST, "Malformed JSON",
        "Malformed error request or invalid data type",
        null,
        request);

    return ResponseEntity.badRequest().body(problemDetail);
  }
}
