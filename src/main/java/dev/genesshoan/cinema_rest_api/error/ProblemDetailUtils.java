package dev.genesshoan.cinema_rest_api.error;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import jakarta.servlet.http.HttpServletRequest;

public class ProblemDetailUtils {
  public static <T, K, V> Map<K, List<V>> groupErrors(
      Collection<T> items,
      Function<T, K> keyMapper,
      Function<T, V> valueMapper) {
    return items.stream()
        .collect(Collectors.groupingBy(
            keyMapper,
            Collectors.mapping(valueMapper, Collectors.toList())));
  }

  public static ProblemDetail errorResponse(
      HttpStatus status,
      String title,
      String detail,
      Map<String, ?> errors,
      HttpServletRequest request) {
    ProblemDetail pd = ProblemDetail.forStatus(status);
    pd.setTitle(title);
    pd.setDetail(detail);
    pd.setProperty("errors", errors);
    pd.setProperty("timestamp", LocalDateTime.now());
    pd.setProperty("path", request.getRequestURI());

    return pd;
  }
}
