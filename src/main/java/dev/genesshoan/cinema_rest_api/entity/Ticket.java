package dev.genesshoan.cinema_rest_api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing a cinema ticket purchase.
 *
 * <p>
 * A ticket represents a customer's reservation or purchase of a specific seat
 * for a showtime. Each ticket is associated with exactly one seat and contains
 * information about the purchase date, price, customer name, and ticket status.
 * </p>
 *
 * <p>
 * Database table: {@code tickets}
 * </p>
 *
 * <p>
 * Unique constraints:
 * <ul>
 * <li>Each ticket must be associated with a unique seat to prevent
 * double-booking</li>
 * </ul>
 * </p>
 *
 * <p>
 * Relationships:
 * <ul>
 * <li>Many-to-One with {@link Seat}: Each ticket is for exactly one seat</li>
 * </ul>
 * </p>
 *
 * @see Seat
 * @see TicketStatus
 * @since 1.0.0
 */
@Entity
@Table(name = "tickets", uniqueConstraints = {
    @UniqueConstraint(name = "uk_ticket_seat", columnNames = { "seat_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
  /**
   * The unique identifier for this ticket.
   *
   * Generated automatically by the database using an identity strategy.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The date and time when this ticket was purchased.
   *
   * <p>
   * Automatically set to the current timestamp when the ticket is created
   * via the {@link #onCreate()} method.
   * </p>
   */
  @Column(name = "purcharse_date", nullable = false)
  private LocalDateTime purcharse;

  /**
   * The price paid for this ticket.
   *
   * <p>
   * Represents the amount charged to the customer for this seat reservation.
   * Must be non-null.
   * </p>
   */
  @Column(name = "price", nullable = false)
  private BigDecimal price;

  /**
   * The name of the customer who purchased this ticket.
   *
   * <p>
   * Maximum length: 255 characters. Must be non-null.
   * </p>
   */
  @Column(name = "customerName", length = 255, nullable = false)
  private String customerName;

  /**
   * The current status of this ticket.
   *
   * <p>
   * Tracks whether the ticket is ACTIVE, CANCELLED, or USED.
   * Must be non-null.
   * </p>
   *
   * @see TicketStatus
   */
  @Column(nullable = false)
  private TicketStatus status;

  /**
   * The seat associated with this ticket.
   *
   * <p>
   * This is the owning side of the relationship. The foreign key
   * {@code seat_id} is stored in the {@code tickets} table.
   * This relationship is mandatory - every ticket must be for exactly one seat.
   * </p>
   *
   * @see Seat#tickets
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "seat_id", nullable = false)
  private Seat seat;

  /**
   * Lifecycle callback that sets the purchase date to the current timestamp
   * before the entity is persisted to the database.
   *
   * <p>
   * Automatically invoked by JPA before the ticket is first saved.
   * </p>
   */
  @PrePersist
  protected void onCreate() {
    purcharse = LocalDateTime.now();
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
    Ticket other = (Ticket) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
