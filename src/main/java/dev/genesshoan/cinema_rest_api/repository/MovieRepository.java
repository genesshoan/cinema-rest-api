package dev.genesshoan.cinema_rest_api.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;

/**
 * Spring Data JPA repository for {@link Movie} entities.
 * 
 * Provides standard CRUD operations and custom query methods for
 * movie persistence and retrieval.
 * 
 * @see Movie
 * @since 1.0.0
 */
public interface MovieRepository extends JpaRepository<Movie, Long> {
  /**
   * Checks if a movie with the given title and release date exists.
   * 
   * The comparison is case-sensitive for both title and release date.
   * 
   * @param title       the movie title to check
   * @param releaseDate the release date to check
   * @return {@code true} if a movie with the same title and release date exists,
   *         {@code false} otherwise
   */
  public boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);

  /**
   * Searches for movies by title and/or genre with partial matching.
   * 
   * This query uses case-insensitive LIKE matching for both parameters.
   * If a parameter is null, it is ignored in the search criteria.
   * Only active movies are returned.
   *
   * <p>
   * Query behavior:
   * <ul>
   * <li>title = null, genre = null → returns all active movies</li>
   * <li>title = "sun", genre = null → returns active movies containing "sun" in
   * title</li>
   * <li>title = null, genre = "rom" → returns active movies containing "rom" in
   * genre</li>
   * <li>title = "sun", genre = "rom" → returns active movies matching both criteria</li>
   * </ul>
   * </p>
   * 
   * @param title    optional title search term (case-insensitive, partial match)
   * @param genre    optional genre search term (case-insensitive, partial match)
   * @param pageable pagination and sorting parameters
   * @return a page of active movies matching the search criteria
   */
  @Query("""
        SELECT m
        FROM Movie m
        WHERE m.active = true
          AND (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%')))
      """)
  public Page<Movie> search(
      @Param("title") String title,
      @Param("genre") String genre,
      Pageable pageable);

  /**
   * Finds all active movie IDs that have at least one showtime matching the given status
   * and with a show date-time greater than or equal to the specified date.
   *
   * <p>
   * This query is paginated and returns a Page of Long IDs. The IDs can be
   * used to fetch the full Movie entities with their showtimes in a separate
   * query. Only active movies are included.
   * </p>
   *
   * @param from     the base date-time to filter showtimes
   * @param status   the status of the showtimes to filter (e.g., AVAILABLE,
   *                 CANCELLED)
   * @param pageable pagination and sorting parameters
   * @return a Page of active movie IDs matching the criteria
   */
  @Query("""
        SELECT DISTINCT m.id
        FROM Movie m
        JOIN m.showtimes s
        WHERE m.active = true
          AND s.startTime >= :from
          AND s.status = :status
      """)
  public Page<Long> findMovieIdsWithShowtimes(
      @Param("from") LocalDateTime from,
      @Param("status") ShowtimeStatus status,
      Pageable pageable);

  /**
   * Finds full active Movie entities for the given list of movie IDs and fetches
   * all their showtimes in a single query.
   *
   * <p>
   * This method is typically used after fetching movie IDs with pagination,
   * to avoid N+1 query problems when accessing showtimes. The results
   * will contain one Movie object per ID, even if the movie has multiple
   * showtimes. Only active movies are included.
   * </p>
   *
   * @param ids the list of movie IDs to fetch
   * @return a list of active Movie entities with their showtimes loaded
   */
  @Query("""
        SELECT DISTINCT m
        FROM Movie m
        LEFT JOIN FETCH m.showtimes
        WHERE m.id IN :ids
          AND m.active = true
      """)
  public List<Movie> findMovieWithShowtimes(
      @Param("ids") List<Long> ids);

  /**
   * Checks if a movie has any active (scheduled or not yet completed) showtimes.
   *
   * A movie is considered to have active showtimes if it has at least one
   * showtime with status SCHEDULED.
   *
   * @param movieId the ID of the movie to check
   * @return {@code true} if the movie has active showtimes, {@code false} otherwise
   */
  @Query("""
        SELECT COUNT(s) > 0
        FROM Movie m
        JOIN m.showtimes s
        WHERE m.id = :movieId
          AND s.status = 'SCHEDULED'
      """)
  public boolean hasActiveShowtimes(@Param("movieId") Long movieId);
}
