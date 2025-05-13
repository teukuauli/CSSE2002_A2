package game.core;

/**
 * Represents a movable and interactive object in the space game that moves downwards.
 */
public abstract class DescendingEnemy extends ObjectWithPosition {

    /**
     * Creates a movable and interactive object at the given coordinates that moves downwards.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public DescendingEnemy(int x, int y) {
        super(x, y);
    }

    /**
     * Moves the DescendingEnemy downwards by one if the given tick is a multiple of 10.
     *
     * @param tick the given game tick.
     */
    @Override
    public void tick(int tick) {
        if (tick % 10 == 0) {
            y++; // Move downward
        }
    }
}
