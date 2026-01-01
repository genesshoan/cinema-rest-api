package dev.genesshoan.cinema_rest_api.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Handler for validation-related exceptions (request/parameter/body validation).
 */
@ControllerAdvice
@Order(1)
public class ValidationExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

  /**
   * Thrown when Bean Validation (@Valid) fails on request body DTOs.
   * Typical cases:
   * - Missing required fields
   * - Invalid field formats
   * - Size / pattern violations
   *
   * Returns HTTP 400 with a map of field -> messages.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationException(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    log.info("Validation failed for request body: {} {} -> {} fields", request.getMethod(), request.getRequestURI(), ex.getFieldErrorCount());

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
   *
   * Why this can occur:
   * - Constraints on method parameters (size, pattern, min/max) are violated.
   * - Invalid query parameters passed by the client.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ProblemDetail> handleConstraintViolationException(
      ConstraintViolationException ex,
      HttpServletRequest request) {
    log.info("Constraint violation on method parameters: {} {} -> {} violations", request.getMethod(), request.getRequestURI(), ex.getConstraintViolations().size());

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

  /**
   * Handle cases where a path or request parameter cannot be converted to the
   * required type (e.g. passing "abc" where an integer is expected).
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handlePathVariableTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      HttpServletRequest request) {
    log.info("Argument type mismatch: {} {} -> param {} value {} expected {}", request.getMethod(), request.getRequestURI(), ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

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

  /**
   * Handle missing required query/form parameters.
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ProblemDetail> handleMissingRequestParam(
      MissingServletRequestParameterException ex,
      HttpServletRequest request) {
    log.info("Missing request parameter: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getParameterName());

    ProblemDetail pd = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST,
        "Missing request parameter",
        String.format("Required parameter '%s' is missing", ex.getParameterName()),
        null,
        request);

    return ResponseEntity.badRequest().body(pd);
  }

  /**
   * Handle malformed JSON or unreadable request bodies.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpNotReadableException(
      HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    log.info("Malformed JSON or unreadable request: {} {} -> {}", request.getMethod(), request.getRequestURI(), ex.getMessage());

    ProblemDetail problemDetail = ProblemDetailUtils.errorResponse(
        HttpStatus.BAD_REQUEST, "Malformed JSON",
        "Malformed error request or invalid data type",
        null,
        request);

    return ResponseEntity.badRequest().body(problemDetail);
  }
}
