package dev.genesshoan.cinema_rest_api.movie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import dev.genesshoan.cinema_rest_api.dto.MovieRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.MovieResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.entity.ShowtimeStatus;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.MovieMapper;
import dev.genesshoan.cinema_rest_api.repository.MovieRepository;
import dev.genesshoan.cinema_rest_api.service.MovieService;

/**
 * Unit tests for {@link MovieService}.
 *
 * <p>These tests verify the behavior of the service layer responsible for
 * creating, retrieving, updating, deleting, and searching movies. Each test is
 * focused on a single behavior and uses Mockito to isolate the service from its
 * dependencies.</p>
 */
@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
  @Mock
  private MovieRepository movieRepository;

  @Mock
  private MovieMapper movieMapper;

  @InjectMocks
  private MovieService movieService;

  private MovieRequestDTO requestDTO;
  private MovieResponseDTO responseDTO;
  private Movie movie;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    movie = new Movie();
    movie.setId(1L);
    movie.setTitle("Before Sunrise");
    movie.setGenre("Romance");
    movie.setDescription(
        "A young man and woman meet on a train in Europe and spend one romantic evening together in Vienna");
    movie.setDurationMinutes(101);
    movie.setReleaseDate(LocalDate.of(1995, 1, 27));

    requestDTO = new MovieRequestDTO(
        "Before Sunrise",
        101,
        "Romance",
        LocalDate.of(1995, 1, 27),
        "A young man and woman meet on a train in Europe and spend one romantic evening together in Vienna");

    responseDTO = new MovieResponseDTO(
        1L,
        "Before Sunrise",
        101,
        "Romance",
        LocalDate.of(1995, 1, 27),
        "A young man and woman meet on a train in Europe and spend one romantic evening together in Vienna");

    pageable = PageRequest.of(0, 2);
  }

  /**
   * Verifies that when a movie does not exist, the service persists the
   * movie and returns a populated {@link MovieResponseDTO}.
   */
  @Test
  @DisplayName("If the movie does not already exist, should save the movie and return MovieResponseDTO")
  void createMovie_WhenDoesNotExist_ShouldSaveTheMovie() {
    when(movieMapper.toEntity(requestDTO)).thenReturn(movie);
    when(movieRepository.existsByTitleAndReleaseDate("Before Sunrise", LocalDate.of(1995, 1, 27)))
        .thenReturn(false);
    when(movieRepository.save(any(Movie.class))).thenReturn(movie);
    when(movieMapper.toDto(movie)).thenReturn(responseDTO);

    MovieResponseDTO result = movieService.createMovie(requestDTO);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("Before Sunrise");
    assertThat(result.genre()).isEqualTo("Romance");
    assertThat(result.durationMinutes()).isEqualTo(101);
    assertThat(result.releaseDate()).isEqualTo(LocalDate.of(1995, 1, 27));

    verify(movieMapper).toEntity(requestDTO);
    verify(movieRepository).existsByTitleAndReleaseDate("Before Sunrise", LocalDate.of(1995, 1, 27));
    verify(movieRepository).save(any(Movie.class));
    verify(movieMapper).toDto(movie);
  }

  /**
   * Verifies that attempting to create a movie that already exists causes a
   * {@link ResourceAlreadyExistsException} to be thrown and no save operation
   * to occur.
   */
  @Test
  @DisplayName("If the movie already exists, should throw ResourceAlreadyExistsException")
  void createMovie_WhenAlreadyExists_ShouldThrowException() {
    when(movieMapper.toEntity(requestDTO)).thenReturn(movie);
    when(movieRepository.existsByTitleAndReleaseDate("Before Sunrise", LocalDate.of(1995, 1, 27)))
        .thenReturn(true);

    assertThatThrownBy(() -> movieService.createMovie(requestDTO))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("Movie with title Before Sunrise already exists");

    verify(movieMapper).toEntity(requestDTO);
    verify(movieRepository).existsByTitleAndReleaseDate(anyString(), any(LocalDate.class));
    verify(movieRepository, never()).save(any(Movie.class));
    verify(movieMapper, never()).toDto(any(Movie.class));
  }

  /**
   * Verifies that a movie can be retrieved by its identifier and is mapped
   * to {@link MovieResponseDTO}.
   */
  @Test
  @DisplayName("If a movie exists with the given id, should return MovieResponseDTO")
  void getMovieById_WhenExists_ShouldReturnMovieResponseDTO() {
    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(movieMapper.toDto(movie)).thenReturn(responseDTO);

    MovieResponseDTO result = movieService.getMovieById(1L);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("Before Sunrise");

    verify(movieRepository).findById(1L);
    verify(movieMapper).toDto(movie);
  }

  /**
   * Verifies that requesting a non-existent movie id results in a
   * {@link ResourceNotFoundException}.
   */
  @Test
  @DisplayName("If a movie does not exist with the given id, should throw ResourceNotFoundException")
  void getMovieById_WhenNotExists_ShouldThrowException() {
    when(movieRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> movieService.getMovieById(1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Movie with id 1 was not found");

    verify(movieRepository).findById(1L);
    verify(movieMapper, never()).toDto(any(Movie.class));
  }

  /**
   * Verifies that updating an existing movie applies the changes and returns
   * an updated {@link MovieResponseDTO}.
   */
  @Test
  @DisplayName("If a movie exists, should update and return MovieResponseDTO")
  void updateMovie_WhenExists_ShouldReturnUpdatedMovie() {
    MovieRequestDTO updateRequest = new MovieRequestDTO(
        "Before Sunrise - Updated",
        120,
        "Drama",
        LocalDate.of(1995, 1, 27),
        "Updated description");

    Movie updatedMovie = new Movie();
    updatedMovie.setId(1L);
    updatedMovie.setTitle("Before Sunrise - Updated");
    updatedMovie.setDurationMinutes(120);
    updatedMovie.setGenre("Drama");
    updatedMovie.setReleaseDate(LocalDate.of(1995, 1, 27));
    updatedMovie.setDescription("Updated description");

    MovieResponseDTO updatedResponse = new MovieResponseDTO(
        1L,
        "Before Sunrise - Updated",
        120,
        "Drama",
        LocalDate.of(1995, 1, 27),
        "Updated description");

    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);
    when(movieMapper.toDto(updatedMovie)).thenReturn(updatedResponse);

    MovieResponseDTO result = movieService.updateMovie(1L, updateRequest);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("Before Sunrise - Updated");
    assertThat(result.durationMinutes()).isEqualTo(120);
    assertThat(result.genre()).isEqualTo("Drama");

    verify(movieRepository).findById(1L);
    verify(movieRepository).save(any(Movie.class));
    verify(movieMapper).toDto(updatedMovie);
  }

  /**
   * Verifies that attempting to update a non-existent movie results in a
   * {@link ResourceNotFoundException} and that no save operation occurs.
   */
  @Test
  @DisplayName("If a movie does not exist, update should throw ResourceNotFoundException")
  void updateMovie_WhenNotExists_ShouldThrowException() {
    when(movieRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> movieService.updateMovie(1L, requestDTO))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Movie with id 1 does not exist");

    verify(movieRepository).findById(1L);
    verify(movieRepository, never()).save(any(Movie.class));
  }

  /**
   * Verifies that attempting to update a movie where the title or release date
   * is changed to values that already exist will result in a
   * {@link ResourceAlreadyExistsException} and that the update will not be
   * persisted.
   */
  @Test
  @DisplayName("If update changes title/releaseDate to an existing movie, should throw ResourceAlreadyExistsException")
  void updateMovie_WhenConflictOnTitleAndDate_ShouldThrowException() {
    MovieRequestDTO updateRequest = new MovieRequestDTO(
        "Before Sunrise - Duplicate",
        101,
        "Romance",
        LocalDate.of(1995, 1, 27),
        "Some description");

    when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
    when(movieRepository.existsByTitleAndReleaseDate(updateRequest.title(), updateRequest.releaseDate()))
        .thenReturn(true);

    assertThatThrownBy(() -> movieService.updateMovie(1L, updateRequest))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("A movie with title");

    verify(movieRepository).findById(1L);
    verify(movieRepository, never()).save(any(Movie.class));
  }

  static Stream<Arguments> searchArguments() {
    return Stream.of(
        Arguments.of(null, null),
        Arguments.of("Matrix", null),
        Arguments.of(null, "Sci-Fi"),
        Arguments.of("Matrix", "Sci-Fi"));
  }

  /**
   * Parameterized test that verifies search behavior for different combinations
   * of title and genre input values. The service should return a page of
   * {@link MovieResponseDTO} objects matching the search criteria.
   *
   * @param title optional title filter used for the search
   * @param genre optional genre filter used for the search
   */
  @ParameterizedTest
  @MethodSource("searchArguments")
  @DisplayName("Search should return Page of MovieResponseDTO")
  void search_ShouldReturnExpectedPage(String title, String genre) {
    List<Movie> movies = List.of(movie, movie);
    Page<Movie> moviePage = new PageImpl<>(movies, pageable, movies.size());

    when(movieRepository.search(title, genre, pageable)).thenReturn(moviePage);
    when(movieMapper.toDto(any(Movie.class))).thenReturn(responseDTO);

    Page<MovieResponseDTO> result = movieService.search(title, genre, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent()).extracting(MovieResponseDTO::title).contains("Before Sunrise");
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(2);

    verify(movieRepository).search(title, genre, pageable);
    verify(movieMapper, times(2)).toDto(any(Movie.class));
  }

  /**
   * Verifies that when the repository returns no results, the service returns
   * an empty page and does not map any entities to DTOs.
   */
  @Test
  @DisplayName("Search should return empty page when no results are found")
  void search_WhenNoResults_ShouldReturnEmptyPage() {
    when(movieRepository.search(null, null, pageable)).thenReturn(Page.empty(pageable));

    Page<MovieResponseDTO> result = movieService.search(null, null, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();

    verify(movieRepository).search(null, null, pageable);
    verify(movieMapper, never()).toDto(any(Movie.class));
  }

  /**
   * Verifies that deleting a movie which exists results in the repository
   * delete operation being invoked.
   */
  @Test
  @DisplayName("If a movie exists, deleteMovieById should call repository delete")
  void deleteMovieById_WhenExists_ShouldDelete() {
    when(movieRepository.existsById(1L)).thenReturn(true);

    movieService.deleteMovieById(1L);

    verify(movieRepository).existsById(1L);
    verify(movieRepository).deleteById(1L);
  }

  /**
   * Verifies that attempting to delete a non-existent movie results in a
   * {@link ResourceNotFoundException} and no delete is attempted.
   */
  @Test
  @DisplayName("If a movie does not exist, deleteMovieById should throw ResourceNotFoundException")
  void deleteMovieById_WhenNotExists_ShouldThrowException() {
    when(movieRepository.existsById(1L)).thenReturn(false);

    assertThatThrownBy(() -> movieService.deleteMovieById(1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Movie with id '1' does not exist");

    verify(movieRepository).existsById(1L);
    verify(movieRepository, never()).deleteById(1L);
  }

  /**
   * Verifies that getMoviesWithShowtimes returns a page of movie DTOs when the
   * repository returns matching ids and entities.
   */
  @Test
  @DisplayName("getMoviesWithShowtimes should return Page of MovieResponseDTO when results exist")
  void getMoviesWithShowtimes_WhenResultsExist_ShouldReturnPage() {
    LocalDateTime from = LocalDateTime.now();
    ShowtimeStatus status = ShowtimeStatus.SCHEDULED;

    List<Long> ids = List.of(1L);
    Page<Long> idPage = new PageImpl<>(ids, pageable, ids.size());

    List<Movie> movies = List.of(movie);

    when(movieRepository.findMovieIdsWithShowtimes(from, status, pageable)).thenReturn(idPage);
    when(movieRepository.findMovieWithShowtimes(ids)).thenReturn(movies);
    when(movieMapper.toDto(movie)).thenReturn(responseDTO);

    Page<MovieResponseDTO> result = movieService.getMoviesWithShowtimes(from, status, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent()).extracting(MovieResponseDTO::id).contains(1L);

    verify(movieRepository).findMovieIdsWithShowtimes(from, status, pageable);
    verify(movieRepository).findMovieWithShowtimes(ids);
    verify(movieMapper).toDto(movie);
  }

  /**
   * Verifies that getMoviesWithShowtimes returns an empty page when repository
   * returns no ids.
   */
  @Test
  @DisplayName("getMoviesWithShowtimes should return empty page when no results")
  void getMoviesWithShowtimes_WhenNoResults_ShouldReturnEmptyPage() {
    LocalDateTime from = LocalDateTime.now();
    ShowtimeStatus status = ShowtimeStatus.SCHEDULED;

    Page<Long> emptyIdPage = Page.empty(pageable);

    when(movieRepository.findMovieIdsWithShowtimes(from, status, pageable)).thenReturn(emptyIdPage);
    when(movieRepository.findMovieWithShowtimes(Collections.emptyList())).thenReturn(Collections.emptyList());

    Page<MovieResponseDTO> result = movieService.getMoviesWithShowtimes(from, status, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();

    verify(movieRepository).findMovieIdsWithShowtimes(from, status, pageable);
    verify(movieRepository).findMovieWithShowtimes(Collections.emptyList());
    verify(movieMapper, never()).toDto(any(Movie.class));
  }
}
