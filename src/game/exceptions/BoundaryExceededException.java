package game.exceptions;

/**
 * An exception indicating a Controllable is attempting to move outside the game boundaries.
 */
public class BoundaryExceededException extends RuntimeException {

    /**
     * Creates a new BoundaryExceeded Exception with the provided message.
     * @param message The provided message
     */
    public BoundaryExceededException(String message) {
        super(message);
    }
}
