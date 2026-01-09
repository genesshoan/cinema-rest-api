package dev.genesshoan.cinema_rest_api.entity;

/**
 * Enum representing the lifecycle state of a {@link Showtime}.
 *
 * - SCHEDULED: showtime is planned and tickets can be sold.
 * - COMPLETED: showtime has finished.
 * - CANCELLED: showtime was cancelled and should not be sold or displayed.
 *
 * These values are persisted as strings in the database.
 */
public enum ShowtimeStatus {
  SCHEDULED,
  COMPLETED,
  CANCELLED
}
