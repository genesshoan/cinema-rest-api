package dev.genesshoan.cinema_rest_api.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.genesshoan.cinema_rest_api.entity.Movie;

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
   * 
   * <p>
   * Query behavior:
   * <ul>
   * <li>title = null, genre = null → returns all movies</li>
   * <li>title = "sun", genre = null → returns movies containing "sun" in
   * title</li>
   * <li>title = null, genre = "rom" → returns movies containing "rom" in
   * genre</li>
   * <li>title = "sun", genre = "rom" → returns movies matching both criteria</li>
   * </ul>
   * </p>
   * 
   * @param title    optional title search term (case-insensitive, partial match)
   * @param genre    optional genre search term (case-insensitive, partial match)
   * @param pageable pagination and sorting parameters
   * @return a page of movies matching the search criteria
   */
  @Query("""
        SELECT m
        FROM Movie m
        WHERE (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%')))
      """)
  public Page<Movie> search(
      @Param("title") String title,
      @Param("genre") String genre,
      Pageable pageable);

}
