package dev.genesshoan.cinema_rest_api.entity;

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
 * JPA entity representing a seat in a cinema showtime.
 *
 * <p>
 * A seat is a specific position in a room assigned to a showtime. Each seat is identified
 * by its row number and seat number within that row, and belongs to exactly one showtime.
 * Seats track their availability status (AVAILABLE, RESERVED, OCCUPIED, etc.).
 * </p>
 *
 * <p>
 * Database table: {@code seats}
 * </p>
 *
 * <p>
 * Unique constraints:
 * <ul>
 * <li>The combination of showtime_id, row_number, and seat_number must be unique
 * to ensure no duplicate seats are created for the same showtime position</li>
 * </ul>
 * </p>
 *
 * <p>
 * Relationships:
 * <ul>
 * <li>Many-to-One with {@link Showtime}: Each seat belongs to exactly one showtime</li>
 * </ul>
 * </p>
 *
 * @see Showtime
 * @see SeatStatus
 * @since 1.0.0
 */
@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(name = "uk_seats_showtime_row_seat_number", columnNames = { "showtime_id", "row_number",
        "seat_number" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
  /**
   * The unique identifier for this seat.
   *
   * Generated automatically by the database using an identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The row number of this seat within the showtime's room.
   *
   * <p>
   * Row numbers are 1-indexed and correspond to physical row positions.
   * Must be positive and not null.
   * </p>
   */
  @Column(name = "row_number", nullable = false)
  private Integer rowNumber;

  /**
   * The seat number within its row.
   *
   * <p>
   * Seat numbers are 1-indexed within each row and correspond to physical seat positions.
   * Must be positive and not null.
   * </p>
   */
  @Column(name = "seat_number", nullable = false)
  private Integer seatNumber;

  /**
   * The current availability status of this seat.
   *
   * <p>
   * Tracks whether the seat is AVAILABLE for booking, RESERVED, OCCUPIED, or in another state.
   * Defaults to {@link SeatStatus#AVAILABLE} when created.
   * </p>
   *
   * @see SeatStatus
   */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SeatStatus status = SeatStatus.AVAILABLE;

  /**
   * The showtime to which this seat belongs.
   *
   * <p>
   * This is the owning side of the relationship. The foreign key
   * {@code showtime_id} is stored in the {@code seats} table.
   * This relationship is mandatory - every seat must belong to exactly one showtime.
   * </p>
   *
   * @see Showtime#seats
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "showtime_id", nullable = false)
  private Showtime showtime;

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
    Seat other = (Seat) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
