package game.ui;

/**
 * Represents an object that benefits from updating each game tick.
 */
public interface Tickable {
    /**
     * Applies changes made for a given game tick.
     *
     * @param tick the given game tick.
     */
    void tick(int tick);
}
