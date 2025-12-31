package dev.genesshoan.cinema_rest_api.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a movie available in the system.
 * Does not include showtimes or schedules (see {@link Showtime}).
 */
@Entity
public class Movie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotBlank
  @Column(nullable = false, length = 255)
  String title;

  @NotNull
  @Column(name = "duration_minutes", nullable = false)
  Integer durationMinutes;

  @Column(length = 30)
  String genre;

  @NotNull
  @Column(name = "realese_date", nullable = false)
  LocalDate realeseDate;

  @Column(columnDefinition = "TEXT")
  String description;

  public Movie() {
  }

  public Movie(Long id, @NotBlank String title, @NotNull Integer durationMinutes, String genre,
      @NotNull LocalDate realeseDate, String description) {
    this.id = id;
    this.title = title;
    this.durationMinutes = durationMinutes;
    this.genre = genre;
    this.realeseDate = realeseDate;
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

  public LocalDate getRealeseDate() {
    return realeseDate;
  }

  public void setRealeseDate(LocalDate realeseDate) {
    this.realeseDate = realeseDate;
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
    result = prime * result + ((durationMinutes == null) ? 0 : durationMinutes.hashCode());
    result = prime * result + ((genre == null) ? 0 : genre.hashCode());
    result = prime * result + ((realeseDate == null) ? 0 : realeseDate.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
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
    if (durationMinutes == null) {
      if (other.durationMinutes != null)
        return false;
    } else if (!durationMinutes.equals(other.durationMinutes))
      return false;
    if (genre == null) {
      if (other.genre != null)
        return false;
    } else if (!genre.equals(other.genre))
      return false;
    if (realeseDate == null) {
      if (other.realeseDate != null)
        return false;
    } else if (!realeseDate.equals(other.realeseDate))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    return true;
  }
}
