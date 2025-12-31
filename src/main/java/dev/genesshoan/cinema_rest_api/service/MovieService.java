package dev.genesshoan.cinema_rest_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.repository.MovieRepository;

@Service
public class MovieService {
  private final MovieRepository movieRepository;

  public MovieService(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  public Movie createMovie(Movie movie) {
    if (movieRepository.existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())) {
      throw new ResourceAlreadyExistsException("Movie with title " + movie.getTitle() + " already exists");
    }

    return movieRepository.save(movie);
  }

  // TODO: Implement public List<Movies> getMoviesInTheaters() when Showtime is
  // ready :)

  public List<Movie> getAllMovies() {
    return movieRepository.findAll();
  }

  public Movie getMovieById(Long id) {
    return movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " was not found"));
  }

  public Movie updateMovie(Long id, Movie movie) {
    Movie existing = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " does not exists"));

    existing.setTitle(movie.getTitle());
    existing.setDescription(movie.getDescription());
    existing.setDurationMinutes(movie.getDurationMinutes());
    existing.setGenre(movie.getGenre());
    existing.setReleaseDate(movie.getReleaseDate());

    return movieRepository.save(existing);
  }

  // TODO: Implement public Movie deleteMovieById(Long id) when showtime is ready

  public List<Movie> search(String title, String genre) {
    return movieRepository.search(title, genre);
  }
}
