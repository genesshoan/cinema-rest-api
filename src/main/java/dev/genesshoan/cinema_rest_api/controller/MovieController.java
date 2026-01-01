package dev.genesshoan.cinema_rest_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.genesshoan.cinema_rest_api.entity.Movie;
import dev.genesshoan.cinema_rest_api.service.MovieService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/movies")
@Validated
public class MovieController {
  private final MovieService movieService;

  public MovieController(MovieService movieService) {
    this.movieService = movieService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Movie createMovie(@Valid @RequestBody Movie movie) {
    return movieService.createMovie(movie);
  }

  @GetMapping("/{id}")
  public Movie getMovieById(@PathVariable @Min(value = 1, message = "{id.min}") Long id) {
    return movieService.getMovieById(id);
  }

  @GetMapping
  public List<Movie> search(
      @RequestParam(required = false) @Size(min = 1, max = 255, message = "{movie.title.size}") String title,

      @RequestParam(required = false) @Size(min = 1, max = 30, message = "{movie.genre.size}") String genre) {
    return movieService.search(title, genre);
  }

  @PutMapping("/{id}")
  public Movie updateMovie(
      @PathVariable @Min(value = 1, message = "{id.min}") Long id,
      @Valid @RequestBody Movie movie) {
    return movieService.updateMovie(id, movie);
  }
}
