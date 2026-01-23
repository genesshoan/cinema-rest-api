package dev.genesshoan.cinema_rest_api.entity;

/**
 * Enumeration representing the possible states of a seat in a showtime.
 * 
 * <p>Used to track seat availability and prevent double-booking.</p>
 * 
 * @see dev.genesshoan.cinema_rest_api.entity.Seat
 */
public enum SeatStatus {
  /**
   * Seat is available for purchase or reservation.
   */
  AVAILABLE,
  
  /**
   * Seat has been sold and is no longer available.
   */
  SOLD
}
