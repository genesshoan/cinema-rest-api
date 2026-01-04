package dev.genesshoan.cinema_rest_api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import dev.genesshoan.cinema_rest_api.dto.MovieRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.MovieResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.exception.ResourceAlreadyExistsException;
import dev.genesshoan.cinema_rest_api.exception.ResourceNotFoundException;
import dev.genesshoan.cinema_rest_api.mapper.MovieMapper;
import dev.genesshoan.cinema_rest_api.repository.MovieRepository;

@Service
public class MovieService {
  private final MovieRepository movieRepository;
  private final MovieMapper movieMapper;

  public MovieService(MovieRepository movieRepository, MovieMapper movieMapper) {
    this.movieRepository = movieRepository;
    this.movieMapper = movieMapper;
  }

  public MovieResponseDTO createMovie(MovieRequestDTO movieRequestDTO) {
    Movie movie = movieMapper.toEntity(movieRequestDTO);

    if (movieRepository.existsByTitleAndReleaseDate(movie.getTitle(), movie.getReleaseDate())) {
      throw new ResourceAlreadyExistsException("Movie with title " + movie.getTitle() + " already exists");
    }

    return movieMapper.toDto(
        movieRepository.save(movie));
  }

  // TODO: Implement public List<Movies> getMoviesInTheaters() when Showtime is
  // ready :)

  public MovieResponseDTO getMovieById(Long id) {
    Movie movie = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " was not found"));

    return movieMapper.toDto(movie);
  }

  public MovieResponseDTO updateMovie(Long id, MovieRequestDTO movieRequestDTO) {
    Movie existing = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " does not exist"));

    existing.setTitle(movieRequestDTO.title());
    existing.setDescription(movieRequestDTO.description());
    existing.setDurationMinutes(movieRequestDTO.durationMinutes());
    existing.setGenre(movieRequestDTO.genre());
    existing.setReleaseDate(movieRequestDTO.releaseDate());

    return movieMapper.toDto(movieRepository.save(existing));
  }

  // TODO: Implement public Movie deleteMovieById(Long id) when showtime is ready

  public Page<MovieResponseDTO> search(String title, String genre, Pageable pageable) {
    return movieRepository.search(title, genre, pageable)
        .map(movieMapper::toDto);
  }
}
