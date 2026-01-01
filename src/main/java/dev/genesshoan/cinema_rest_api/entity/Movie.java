package dev.genesshoan.cinema_rest_api.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/**
 * Represents a movie available in the system.
 * Does not include showtimes or schedules (see {@link Showtime}).
 */
@Entity
@Table(name = "movies", indexes = {
    @Index(name = "idx_movie_title", columnList = "title, release_date", unique = true)
})
public class Movie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "{movie.title.required}")
  @Size(min = 1, max = 255, message = "{movie.title.size}")
  @Column(nullable = false, length = 255)
  private String title;

  @NotNull(message = "{movie.durationMinutes.required}")
  @Min(value = 1, message = "{movie.durationMinutes.min}")
  @Column(name = "duration_minutes", nullable = false)
  private Integer durationMinutes;

  @NotBlank(message = "{movie.genre.required}")
  @Size(max = 30, message = "movie.genre.size")
  @Column(nullable = false, length = 30)
  private String genre;

  @NotNull(message = "{movie.releaseDate.required}")
  @PastOrPresent(message = "{movie.releaseDate.past}")
  @Column(name = "release_date", nullable = false)
  private LocalDate releaseDate;

  @Size(max = 5000, message = "{movie.description.size}")
  @Column(columnDefinition = "TEXT")
  private String description;

  public Movie() {
  }

  public Movie(Long id, @NotBlank String title, @NotNull Integer durationMinutes, String genre,
      @NotNull LocalDate releaseDate, String description) {
    this.id = id;
    this.title = title;
    this.durationMinutes = durationMinutes;
    this.genre = genre;
    this.releaseDate = releaseDate;
    this.description = description;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getDurationMinutes() {
    return durationMinutes;
  }

  public void setDurationMinutes(Integer durationMinutes) {
    this.durationMinutes = durationMinutes;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(LocalDate realeseDate) {
    this.releaseDate = realeseDate;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((title == null) ? 0 : title.hashCode());
    result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Movie other = (Movie) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (title == null) {
      if (other.title != null)
        return false;
    } else if (!title.equals(other.title))
      return false;
    if (releaseDate == null) {
      if (other.releaseDate != null)
        return false;
    } else if (!releaseDate.equals(other.releaseDate))
      return false;
    return true;
  }
}
