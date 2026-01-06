package dev.genesshoan.cinema_rest_api.entity;

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
 * JPA entity representing a room in the cinema system.
 * 
 * A room contains basic information such as name, rows and seats per rows.
 * Movies are uniquely identified by name.
 * 
 * <p>
 * Database table: {@code rooms}
 * </p>
 * 
 * <p>
 * Unique constraints:
 * <ul>
 * <li>Name must be unique</li>
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
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_name", columnList = "name", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
  /**
   * The unique identifier for this room.
   *
   * Generated automatically by the database using an identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * A unique identifier for this room.
   *
   * Maximum length is 255 characters.
   */
  @Column(nullable = false, length = 255)
  private String name;

  /**
   * Number of rows of this room.
   */
  @Column(nullable = false)
  private Integer rows;

  /**
   * Number of seats per row.
   */
  @Column(name = "seats_per_row", nullable = false)
  private Integer seatsPerRow;

  /**
   * Showtimes associated with this room.
   *
   * <p>
   * This represents a one-to-many relationship where a room may have zero or
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
   * Cascade operations are enabled so that persisting or removing a room
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
  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Showtime> showtimes = new ArrayList<>();

  public void addShowtime(Showtime showtime) {
    showtimes.add(showtime);
    showtime.setRoom(this);
  }

  public void removeShowtime(Showtime showtime) {
    showtimes.remove(showtime);
    showtime.setRoom(null);
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
    Room other = (Room) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
