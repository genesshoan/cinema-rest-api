package dev.genesshoan.cinema_rest_api.mapper;

import org.springframework.stereotype.Component;

import dev.genesshoan.cinema_rest_api.dto.MovieRequestDTO;
import dev.genesshoan.cinema_rest_api.dto.MovieResponseDTO;
import dev.genesshoan.cinema_rest_api.entity.Movie;

@Component
public class MovieMapper {
  public MovieResponseDTO toDto(Movie movie) {
    return new MovieResponseDTO(
        movie.getId(),
        movie.getTitle(),
        movie.getDurationMinutes(),
        movie.getGenre(),
        movie.getReleaseDate(),
        movie.getDescription());
  }

  public Movie toEntity(MovieRequestDTO movieRequestDTO) {
    Movie movie = new Movie();
    movie.setTitle(movieRequestDTO.title());
    movie.setDurationMinutes(movieRequestDTO.durationMinutes());
    movie.setGenre(movieRequestDTO.genre());
    movie.setReleaseDate(movieRequestDTO.releaseDate());
    movie.setDescription(movieRequestDTO.description());

    return movie;
  }
}
