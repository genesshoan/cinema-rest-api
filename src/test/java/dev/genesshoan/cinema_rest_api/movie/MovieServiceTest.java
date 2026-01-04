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

import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.repository.MovieRepository;
import dev.genesshoan.cinema_rest_api.service.MovieService;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
  @Mock
  MovieRepository movieRepository;

  @InjectMocks
  MovieService movieService;

  Movie movie;

  Pageable pageable = PageRequest.of(0, 2);

  List<Movie> movies;

  Page<Movie> page;

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

    movies = List.of(
        movie,
        movie);

    page = new PageImpl<>(movies, pageable, movies.size());
  }

  @Test
  @DisplayName("If the movie does not already exists, should save the movie")
  void createUser_WhenDoesNotExists_ShouldSaveTheMovie() {
    when(movieRepository.existsByTitleAndReleaseDate(
        movie.getTitle(), movie.getReleaseDate())).thenReturn(false);

    when(movieRepository.save(movie)).thenReturn(movie);

    Movie result = movieService.createMovie(movie);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo("Before Sunrise");
    assertThat(result.getGenre()).isEqualTo("Romance");
    assertThat(result.getDescription())
        .isEqualTo("A young man and woman meet on a train in Europe and spend one romantic evening together in Vienna");
    assertThat(result.getDurationMinutes()).isEqualTo(101);
    assertThat(result.getReleaseDate()).isEqualTo(LocalDate.of(1995, 1, 27));

    verify(movieRepository, times(1))
        .existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate());
    verify(movieRepository, times(1)).save(movie);
  }

  @Test
  @DisplayName("If the movie already exists, should throw an exception")
  void createMovie_WhenNotExists_ShouldThrowAnException() {
    when(movieRepository.existsByTitleAndReleaseDate(
        movie.getTitle(),
        movie.getReleaseDate())).thenReturn(false);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(ResourceAlreadyExistsException.class)
        .hasMessageContaining("Movie with title Before Sunrise already exists");

    verify(movieRepository, never()).save(any(Movie.class));

    verify(movieRepository, times(1)).existsByTitleAndReleaseDate(anyString(), any(LocalDate.class));
  }

  @Test
  @DisplayName("If exists a movie with the given id, then should return the movie")
  void getMovieById_WhenExists_ShouldReturnTheMovie() {
    when(movieRepository.findById(1L))
        .thenReturn(Optional.of(movie));

    Movie result = movieService.getMovieById(1L);

    assertThat(result.getId()).isEqualTo(1L);
    assertThat(result.getTitle()).isEqualTo(("Before Sunrise"));
  }

  @Test
  @DisplayName("If does not exists a movie with the given id, then should throw a exception")
  void getMovieById_WhenNotExists_ShouldThrowAnException() {
    when(movieRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> movieService.getMovieById(1L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Movie with id 1 was not found");
  }

  @Test
  @DisplayName("If exists a movie with the given id, then should return the movie")
  void updateMovie_WhenExists_ShouldReturnAMovie() {
    when(movieRepository.findById(1L))
        .thenReturn(Optional.of(movie));

    Movie updatedMovie = new Movie();
    updatedMovie.setId(1L);
    updatedMovie.setTitle("Before Sunrise");
    updatedMovie.setDescription("New description");
    updatedMovie.setDurationMinutes(30);
    updatedMovie.setGenre("New genre");
    updatedMovie.setReleaseDate(LocalDate.now());

    Movie result = movieService.updateMovie(1L, updatedMovie);

    assertThat(result.getId()).isEqualTo(updatedMovie.getId());
    assertThat(result.getTitle()).isEqualTo(updatedMovie.getTitle());
    assertThat(result.getDescription()).isEqualTo(updatedMovie.getDescription());
    assertThat(result.getDurationMinutes()).isEqualTo(updatedMovie.getDurationMinutes());
    assertThat(result.getGenre()).isEqualTo(updatedMovie.getGenre());
    assertThat(result.getReleaseDate()).isEqualTo(updatedMovie.getReleaseDate());

    verify(movieRepository, times(1)).save(any(Movie.class));
  }

  @Test
  @DisplayName("If does not exists a movie with the given id, then should throw a exception")
  void updateMovie_WhenExists_ShouldReturnAnException() {
    when(movieRepository.findById(1L))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> movieService.updateMovie(1L, movie))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Movie with id 1 does not exists");

    verify(movieRepository, never()).save(any(Movie.class));
  }

  static Stream<Arguments> searchArguments() {
    return Stream.of(
        Arguments.of(null, null),
        Arguments.of("Matrix", null),
        Arguments.of(null, "Sci-Fi"),
        Arguments.of("Matrix", "Sci-Fi"));
  }

  @Test
  @ParameterizedTest
  @MethodSource("searchArguments")
  void search_ShouldReturnExpectedPage(String title, String genre) {

    Page<Movie> page = new PageImpl<>(movies, pageable, movies.size());

    when(movieRepository.search(title, genre, pageable))
        .thenReturn(page);

    Page<Movie> result = movieService.search(title, genre, pageable);

    assertThat(result.getContent().size()).isEqualTo(movies.size());
    assertThat(result.getContent().get(0).getTitle())
        .isEqualTo(movies.get(0).getTitle());
    assertThat(result.getNumber()).isEqualTo(page.getNumber());
    assertThat(result.getSize()).isEqualTo(page.getSize());

    verify(movieRepository, times(1)).search(title, genre, pageable);
  }
}
