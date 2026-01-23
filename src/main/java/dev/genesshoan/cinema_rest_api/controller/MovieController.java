package dev.genesshoan.cinema_rest_api.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.genesshoan.cinema_rest_api.dto.movie.MovieRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.movie.MovieResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

/**
 * REST controller for managing cinema movies.
 *
 * <p>
 * This controller provides endpoints for creating, retrieving, updating,
 * searching, and deleting movies in the cinema system. All endpoints return
 * JSON responses and follow RESTful conventions.
 * </p>
 *
 * <p>
 * Base URL: {@code /movies}
 * </p>
 *
 * <p>
 * Supported operations:
 * <ul>
 * <li>Create a new movie</li>
 * <li>Retrieve movie by ID</li>
 * <li>Search movies by title and/or genre</li>
 * <li>Update an existing movie</li>
 * <li>Delete a movie</li>
 * </ul>
 * </p>
 *
 * @see MovieService
 * @see MovieRequestDTO
 * @see MovieResponseDTO
 * @since 1.0.0
 */
@RestController
@RequestMapping("/movies")
@Validated
@AllArgsConstructor
public class MovieController {
  private final MovieService movieService;

  /**
   * Creates a new movie in the system.
   *
   * <p>
   * Validates the request body and creates a new movie if it does not already
   * exist. A movie is considered duplicate when another movie with the same
   * title and release date exists.
   * </p>
   *
   * @param movieRequestDTO the movie request data to create, must not be null
   * @return the created movie with the generated id
   * @throws ResourceAlreadyExistsException if a movie with the same title and
   *                                        release date already exists
   *
   * @see MovieService#createMovie(MovieRequestDTO)
   */
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MovieResponseDTO createMovie(@Valid @RequestBody MovieRequestDTO movieRequestDTO) {
    return movieService.createMovie(movieRequestDTO);
  }

  /**
   * Retrieves a movie by its unique identifier.
   *
   * @param id the movie ID, must be greater than 0
   * @return the movie with the specified id
   * @throws ResourceNotFoundException if no movie with the given ID exists
   *
   * @see MovieService#getMovieById(long)
   */
  @GetMapping("/{id}")
  public MovieResponseDTO getMovieById(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    return movieService.getMovieById(id);
  }

  /**
   * Searches for movies by title and/or genre.
   *
   * <p>
   * This endpoint supports partial, case-insensitive matching for both title and
   * genre. If both parameters are omitted, all movies are returned. Results are
   * paginated and sorted according to the {@code Pageable} parameter.
   * </p>
   *
   * <p>
   * Examples:
   * <ul>
   * <li>{@code GET /movies?title=sunrise} - finds movies containing
   * "sunrise"</li>
   * <li>{@code GET /movies?genre=romance} - finds movies in the romance
   * genre</li>
   * <li>{@code GET /movies?title=before&genre=romance} - combines both
   * filters</li>
   * </ul>
   * </p>
   *
   * @param title    optional title search term (case-insensitive, partial match)
   * @param genre    optional genre search term (case-insensitive, partial match)
   * @param pageable pagination and sorting parameters
   * @return a page of movies matching the search criteria
   *
   * @see MovieService#search(String, String, Pageable)
   */
  @GetMapping
  public Page<MovieResponseDTO> search(
      @RequestParam(required = false) @NotBlank(message = "{movie.title.required}") @Size(max = 255, message = "{movie.title.size}") String title,
      @RequestParam(required = false) @NotBlank(message = "{movie.genre.required}") @Size(max = 30, message = "{movie.genre.size}") String genre,
      Pageable pageable) {
    return movieService.search(title, genre, pageable);
  }

  /**
   * Retrieves all movies that have showtimes matching the given status and
   * with a show date-time greater than or equal to the specified date-time.
   *
   * @param from     the base date-time to filter movies
   * @param status   the showtime status to filter movies
   * @param pageable pagination and sorting parameters
   * @return a page of movies that match the given criteria
   *
   * @see MovieService#getMoviesWithShowtimes(LocalDateTime, ShowtimeStatus,
   *      Pageable)
   */
  @GetMapping("/showtimes")
  public Page<MovieResponseDTO> getMoviesWithShowtimes(
      @RequestParam LocalDateTime from,
      @RequestParam ShowtimeStatus status,
      Pageable pageable) {
    return movieService.getMoviesWithShowtimes(from, status, pageable);
  }

  /**
   * Updates an existing movie.
   *
   * <p>
   * Replaces all fields of the movie with the provided data. The movie must
   * exist before it can be updated.
   * </p>
   *
   * @param id              the ID of the movie to update, must be greater than 0
   * @param movieRequestDTO the new movie data; all fields are validated (see
   *                        {@link MovieRequestDTO})
   * @return the updated movie
   * @throws ResourceNotFoundException      if no movie with the given ID exists
   * @throws ResourceAlreadyExistsException if a different movie with the same
   *                                        title and release date already exists
   *
   * @see MovieService#updateMovie(long, MovieRequestDTO)
   */
  @PutMapping("/{id}")
  public MovieResponseDTO updateMovie(
      @PathVariable @Min(value = 1, message = "{id.min}") long id,
      @Valid @RequestBody MovieRequestDTO movieRequestDTO) {
    return movieService.updateMovie(id, movieRequestDTO);
  }

  /**
   * Deletes a movie by its unique identifier.
   *
   * @param id the ID of the movie to delete; must be greater than 0
   * @throws ResourceNotFoundException if no movie with the specified id exists
   *
   * @see MovieService#deleteMovieById(long)
   */
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteMovieById(@PathVariable @Min(value = 1, message = "{id.min}") long id) {
    movieService.deleteMovieById(id);
  }
}
