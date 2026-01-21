package dev.genesshoan.cinema_rest_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing a showtime in the cinema system.
 * 
 * A showtime contains basic information such as date time and base price.
 * Cannot exist more than one showtime in the same room and date-time.
 *
 * <p>
 * Database table: {@code showtimes}
 * </p>
 * <p>
 * Unique constraint:
 * <ul>
 * <li>Combination of room_id and date_time must be unique</li>
 * </ul>
 *
 * @see Room
 * @see Movie
 * @since 1.0.0
 */
@Entity
@Table(name = "showtimes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_showtimes_room_start_time", columnNames = { "room_id", "start_date_time" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {
  /**
   * The unique identifier for this showtime.
   *
   * Generated automatically by the database using an identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The showtime start date and time.
   *
   * Must be a future or present date.
   * Must be before end date time.
   */
  @Column(name = "start_date_time", nullable = false)
  private LocalDateTime startTime;

  /**
   * The showtime end date and time.
   *
   * Must be a future or present date.
   */
  @Column(name = "end_date_time", nullable = false)
  private LocalDateTime endTime;

  /**
   * The showtime ticket's base price.
   *
   * Must be positive.
   */
  @Column(name = "base_price", nullable = false)
  private BigDecimal basePrice;

  /**
   * The status for this showtime
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  ShowtimeStatus status;

  /**
   * Movie associated with this showtime.
   *
   * <p>
   * This is the owning side of the relationship. The foreign key
   * {@code movie_id} is stored in the {@code showtimes} table.
   * </p>
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "movie_id", nullable = false)
  private Movie movie;

  /**
   * Room in which this showtime takes place.
   *
   * <p>
   * This is the owning side of the relationship. The foreign key
   * {@code room_id} is stored in the {@code showtimes} table.
   * </p>
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private Room room;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Showtime that = (Showtime) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
