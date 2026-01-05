package dev.genesshoan.cinema_rest_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import dev.genesshoan.cinema_rest_api.dto.MovieRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.MovieResponseDTO;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.service.MovieService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * REST controller for managing cinema movies.
 * 
 * This controller provides endpoints for creating, retrieving, updating,
 * and searching movies in the cinema system. All endpoints return JSON
 * responses and follow RESTful conventions.
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
 * <li>Update existing movie</li>
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
public class MovieController {
  private final MovieService movieService;

  public MovieController(MovieService movieService) {
    this.movieService = movieService;
  }

  /**
   * Creates a new movie in the system.
   *
   * Validates the request body and creats a new movie if it does not already
   * exist. A movie is
   * considered duplicate if another movie with the same name and release date
   * exists.
   *
   * @param movieRequestDTO the movie request date to create, must not be null.
   * @return the created movie with the generated id.
   * @throws ResourceAlreadyExistsException  if a movie with the same title and
   *                                         release date already
   *                                         exists.
   * @throws MethodArgumentNotValidException if 'movieRequestDTO' is invalid
   *                                         (automatic validation of request
   *                                         body, {@see MovieRequestDTO})
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
   * @param id the movie ID, must be greater than 0.
   * @return the movie with the specified id.
   * @throws ResourceNotFoundException           if no movie with the given ID
   *                                             exits
   * @throws ConstraintViolationException        if 'id' is less than 1
   * @throws MethodArgumentTypeMismatchException if the path variable 'id' cannot
   *                                             be converted to Long
   *
   * @see MovieService#getMovieById(Long)
   */
  @GetMapping("/{id}")
  public MovieResponseDTO getMovieById(@PathVariable @Min(value = 1, message = "{id.min}") Long id) {
    return movieService.getMovieById(id);
  }

  /**
   * Searches for movies by title and/or genre.
   *
   * This endpoint supports partial matching for both title and genre.
   * If both parameters are null, then returns all movies. Results are paginated
   * and sorted according
   * to Pageable parameter.
   *
   * <p>
   * Examples:
   * <ul>
   * <li>{@code GET /movies?title=sunrise} - finds movies containing
   * "sunrise"</li>
   * <li>{@code GET /movies?genre=romance} - finds movies in romance genre</li>
   * <li>{@code GET /movies?title=before&genre=romance} - combines both
   * filters</li>
   * </ul>
   * </p>
   *
   * @param title    title optional search term (case-insensitive partial match)
   * @param genre    genre optional search term (case-insensitive partial match)
   * @param pageable pagination and sorting parameters
   * @return a page of movies matching the search criteria
   * @throws ConstraintViolationException if 'title' or 'genre' exceed their size
   *                                      limits (automatic validation)
   *
   * @see MovieService#search(String, String, Pageable)
   */
  @GetMapping
  public Page<MovieResponseDTO> search(
      @RequestParam(required = false) @Size(min = 1, max = 255, message = "{movie.title.size}") String title,
      @RequestParam(required = false) @Size(min = 1, max = 30, message = "{movie.genre.size}") String genre,
      Pageable pageable) {
    return movieService.search(title, genre, pageable);
  }

  /**
   * Updates an existing movie.
   *
   * Replaces all fields of the movie with the provided data. The movie must exist
   * in the {@link MovieRepository}
   * before it can be updated.
   *
   * @param id              the ID of the movie to update, must be greater than 0.
   * @param movieRequestDTO the new movie data. All fields are validated,
   *                        {@see MovieRequestDTO}
   * @return the updated movie.
   * @throws ResourceNotFoundException           if no movie with the given ID
   *                                             exists (thrown by the service)
   * @throws ConstraintViolationException        if 'id' is less than 1 (automatic
   *                                             validation of the path variable)
   * @throws MethodArgumentNotValidException     if 'movieRequestDTO' is invalid
   *                                             (automatic validation of the
   *                                             request body,
   *                                             {@see MovieRequestDTO})
   * @throws MethodArgumentTypeMismatchException if the path variable 'id' cannot
   *                                             be converted to Long
   *
   * @see MovieService#updateMovie(Long, MovieRequestDTO)
   */
  @PutMapping("/{id}")
  public MovieResponseDTO updateMovie(
      @PathVariable @Min(value = 1, message = "{id.min}") Long id,
      @Valid @RequestBody MovieRequestDTO movieRequestDTO) {
    return movieService.updateMovie(id, movieRequestDTO);
  }
}
