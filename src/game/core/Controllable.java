package game.core;

import game.exceptions.BoundaryExceededException;
import game.utility.Direction;
import static game.GameModel.*;

/**
 * Represents a controllable object in the space game.
 */
public abstract class Controllable extends ObjectWithPosition {

    /**
     * Creates a controllable object at the given coordinates.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public Controllable(int x, int y) {
        super(x, y);
    }

    /**
     * Moves the Controllable by one in the direction given.<br>
     * Throws BoundaryExceededException if the Controllable is attempting to move outside the game boundaries.<br>
     * A controllable is considered outside the game boundaries if they are at: <br>
     * x-coordinate &gt;= GAME_WIDTH<br>
     * x-coordinate &lt; 0<br>
     * y-coordinate &gt;= GAME_HEIGHT<br>
     * y-coordinate &lt; 0<br>
     * <p>
     * Argument given to the exception is "Cannot move {up/down/left/right}. Out of bounds!" depending on the direction.
     *
     * @param direction the given direction.
     *
     * @throws BoundaryExceededException if attempting to move outside the game boundaries.
     * @hint game dimensions are stored in the model.
     */
    public void move(Direction direction) throws BoundaryExceededException {
        switch (direction) {
            case UP -> {
                if (y <= 0) {
                    throw new BoundaryExceededException("Cannot move up. Out of bounds!");
                }
                y--;
            }
            case DOWN -> {
                if (y + 1 >= GAME_HEIGHT) {
                    throw new BoundaryExceededException("Cannot move down. Out of bounds!");
                }
                y++;
            }
            case LEFT -> {
                if (x <= 0) {
                    throw new BoundaryExceededException("Cannot move left. Out of bounds!");
                }
                x--;
            }
            case RIGHT -> {
                if (x + 1 >= GAME_WIDTH) {
                    throw new BoundaryExceededException("Cannot move right. Out of bounds!");
                }
                x++;
            }
        }
    }
}
