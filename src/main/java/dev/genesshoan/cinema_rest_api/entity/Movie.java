package dev.genesshoan.cinema_rest_api.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

  /**
   * Showtimes associated with this movie.
   *
   * <p>
   * This represents a one-to-many relationship where a movie may have zero or
   * more
   * showtimes, while each showtime must be associated with exactly one movie.
   * </p>
   *
   * <p>
   * This side of the relationship is inverse (non-owning); the foreign key is
   * managed by the {@link Showtime#movie} field.
   * </p>
   *
   * <p>
   * Cascade operations are enabled so that persisting or removing a movie
   * will also affect its showtimes. Orphan removal ensures that showtimes
   * removed from this collection are deleted from the database.
   * </p>
   *
   * <p>
   * This collection is initialized to avoid {@code NullPointerException} and
   * should be modified through helper methods to keep both sides of the
   * association in sync.
   * </p>
   */
  @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Showtime> showtimes = new ArrayList<>();

  public void addShowtime(Showtime showtime) {
    showtimes.add(showtime);
    showtime.setMovie(this);
  }

  public void removeShowtime(Showtime showtime) {
    showtimes.remove(showtime);
    showtime.setMovie(null);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    return true;
  }
}
