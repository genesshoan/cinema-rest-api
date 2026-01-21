package dev.genesshoan.cinema_rest_api.exception;

/**
 * Exception thrown when attempting to delete a resource that is currently
 * being referenced by active related entities.
 *
 * <p>
 * This exception is used to enforce referential integrity and prevent
 * deletion of entities that have active dependencies, such as:
 * </p>
 * <ul>
 * <li>Deleting a movie that has scheduled showtimes</li>
 * <li>Deleting a room that has scheduled showtimes</li>
 * </ul>
 *
 * @since 1.0.0
 */
public class ResourceInUseException extends RuntimeException {
  public ResourceInUseException(String message) {
    super(message);
  }
}

