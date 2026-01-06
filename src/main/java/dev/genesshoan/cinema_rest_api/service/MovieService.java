package dev.genesshoan.cinema_rest_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.genesshoan.cinema_rest_api.dto.MovieRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.MovieResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.MovieMapper;
import dev.genesshoan.cinema_rest_api.repository.MovieRepository;
import lombok.AllArgsConstructor;

/**
 * Service layer for movie business logic.
 * 
 * This service handles all movie-related operations including creation,
 * retrieval, updating, and searching. It enforces business rules such as
 * preventing duplicate movies with the same title and release date.
 * 
 * <p>
 * This service is transactional and performs validation before
 * persisting data to the database.
 * </p>
 * 
 * @see MovieRepository
 * @see MovieMapper
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class MovieService {
  private final MovieRepository movieRepository;
  private final MovieMapper movieMapper;

  /**
   * Creates a new movie in the database.
   * 
   * This method validates that no movie with the same title and release
   * date exists before creating the new movie. The validation is
   * case-sensitive.
   * 
   * @param movieRequestDTO the movie data to create
   * @return the created movie with generated ID
   * @throws ResourceAlreadyExistsException if a movie with the same title
   *                                        and release date already exists in the
   *                                        database
   */
  @Transactional
  public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {
    Movie movie = movieMapper.toEntity(movieRequestDTO);

    if (movieRepository.existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())) {
      throw new ResourceAlreadyExistsException("Movie with title " + movie.getTitle() + " already exists");
    }

    return movieMapper.toDto(
        movieRepository.save(movie));
  }

  /**
   * Retrieves all movies with shows that match the given status and have a show
   * date-time greater than or equal to the specified date.
   *
   * @param from     the base date-time to filter shows
   * @param status   the showtime status to filter shows
   * @param pageable the pagination and sorting parameters
   * @return a page of movies matching the given criteria
   */
  public Page<MovieResponseDTO> getMoviesWithShowtimes(LocalDateTime from, ShowtimeStatus status, Pageable pageable) {
    Page<Long> movieIdsPage = movieRepository.findMovieIdsWithShowtimes(from, status, pageable);
    List<Long> ids = movieIdsPage.getContent();

    List<MovieResponseDTO> dtos = movieRepository.findMovieWithShowtimes(ids).stream()
        .map(movieMapper::toDto)
        .toList();

    return new PageImpl<>(dtos, pageable, movieIdsPage.getTotalElements());
  }

  /**
   * Retrieves a movie by its unique identifier.
   * 
   * @param id the movie ID to search for
   * @return the movie with the specified ID
   * @throws ResourceNotFoundException if no movie with the given ID exists
   */
  public MovieResponseDTO getMovieById(Long id) {
    Movie movie = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " was not found"));

    return movieMapper.toDto(movie);
  }

  /**
   * Updates an existing movie with new data.
   * 
   * All fields of the movie are replaced with the provided data.
   * The movie ID cannot be changed.
   * 
   * @param id              the ID of the movie to update
   * @param movieRequestDTO the new movie data
   * @return the updated movie
   * @throws ResourceNotFoundException if no movie with the given ID exists
   */
  @Transactional
  public MovieResponseDTO updateMovie(Long id, MovieRequestDTO movieRequestDTO) {
    Movie existing = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " does not exist"));

    boolean titleIsChanging = !Objects.equals(existing.getTitle(), movieRequestDTO.title());
    boolean releaseDateIsChanging = !Objects.equals(existing.getReleaseDate(), movieRequestDTO.releaseDate());

    if (titleIsChanging || releaseDateIsChanging) {
      if (movieRepository.existsByTitleAndReleaseDate(
          movieRequestDTO.title(),
          movieRequestDTO.releaseDate())) {
        throw new ResourceAlreadyExistsException(
            String.format(
                "A movie with title '%s' and release date '%s' already exists.  The changes cannot be applied",
                movieRequestDTO.title(),
                movieRequestDTO.releaseDate()));
      }
    }
    existing.setTitle(movieRequestDTO.title());
    existing.setDescription(movieRequestDTO.description());
    existing.setDurationMinutes(movieRequestDTO.durationMinutes());
    existing.setGenre(movieRequestDTO.genre());
    existing.setReleaseDate(movieRequestDTO.releaseDate());

    return movieMapper.toDto(movieRepository.save(existing));
  }

  /**
   * Deletes a movie by its unique identifier.
   *
   * @param id the ID of the movie to update.
   * @throws ResourceNotFoundException if no movie with the given ID exist.
   */
  @Transactional
  public void deleteMovieById(Long id) {
    if (!movieRepository.existsById(id)) {
      throw new ResourceNotFoundException("Movie with id '" + id + "' does not exist");
    }

    movieRepository.deleteById(id);
  }

  /**
   * Searches for movies by title and/or genre using partial matching.
   * 
   * The search is case-insensitive and supports partial matching for
   * both parameters. If both parameters are null, all movies are returned.
   * 
   * @param title    optional title search term, can be null
   * @param genre    optional genre search term, can be null
   * @param pageable pagination and sorting parameters, must not be null
   * @return a page of movies matching the search criteria
   */
  public Page<MovieResponseDTO> search(String title, String genre, Pageable pageable) {
    return movieRepository.search(title, genre, pageable)
        .map(movieMapper::toDto);
  }
}
