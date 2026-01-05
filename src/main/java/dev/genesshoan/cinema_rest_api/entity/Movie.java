package dev.genesshoan.cinema_rest_api.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * JPA entity representing a movie in the cinema system.
 * 
 * A movie contains basic information such as title, duration, genre,
 * release date, and description. Movies are uniquely identified by
 * the combination of title and release date.
 * 
 * <p>
 * Database table: {@code movies}
 * </p>
 * 
 * <p>
 * Unique constraints:
 * <ul>
 * <li>Combination of title and release date must be unique</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Note: This entity does not include showtime information.
 * See {@link Showtime} for scheduling details.
 * </p>
 * 
 * @see Showtime
 * @since 1.0.0
 */
@Entity
@Table(name = "movies", indexes = {
    @Index(name = "idx_movie_title", columnList = "title, release_date", unique = true)
})
public class Movie {
  /**
   * The unique identifier for this movie.
   *
   * Generated automatically by the database using an identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The movie title.
   * 
   * Maximum length is 255 characters. Combined with {@link #releaseDate},
   * this field must be unique across all movies.
   */
  @Column(nullable = false, length = 255)
  private String title;

  /**
   * The duration of the movie in minutes.
   * 
   * Must be a positive integer representing the total runtime.
   */
  @Column(name = "duration_minutes", nullable = false)
  private Integer durationMinutes;

  /**
   * The movie genre or category.
   * 
   * Examples: "Action", "Drama", "Comedy", "Romance", "Horror".
   * Maximum length is 30 characters.
   */
  @Column(nullable = false, length = 30)
  private String genre;

  /**
   * The official release date of the movie.
   * 
   * Combined with {@link #title}, this field must be unique.
   * This allows the same movie title to exist if released on different dates
   * (e.g., original and remake).
   */
  @Column(name = "release_date", nullable = false)
  private LocalDate releaseDate;

  /**
   * Optional detailed description or synopsis of the movie.
   * 
   * Stored as TEXT in the database with no practical length limit.
   * Can be null.
   */
  @Column(columnDefinition = "TEXT")
  private String description;

  public Movie() {
  }

  public Movie(Long id, String title, Integer durationMinutes, String genre,
      LocalDate releaseDate, String description) {
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
