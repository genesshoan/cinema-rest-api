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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.MovieMapper;
import dev.genesshoan.cinema_rest_api.repository.MovieRepository;
import dev.genesshoan.cinema_rest_api.service.MovieService;

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

  static Stream<Arguments> searchArguments() {
    return Stream.of(
        Arguments.of(null, null),
        Arguments.of("Matrix", null),
        Arguments.of(null, "Sci-Fi"),
        Arguments.of("Matrix", "Sci-Fi"));
  }

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
    assertThat(result.getContent().get(0).title()).isEqualTo("Before Sunrise");
    assertThat(result.getNumber()).isEqualTo(0);
    assertThat(result.getSize()).isEqualTo(2);

    verify(movieRepository).search(title, genre, pageable);
    verify(movieMapper, times(2)).toDto(any(Movie.class));
  }
}
