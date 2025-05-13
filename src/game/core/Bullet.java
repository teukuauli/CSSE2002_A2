package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents a bullet fired by the ship.
 */
public class Bullet extends ObjectWithPosition {

    /**
     * Creates a bullet at the given coordinates.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public Bullet(int x, int y) {
        super(x, y);
    }

    /**
     * Moves Bullet upwards by one, regardless of what the provided game tick is.
     *
     * @param tick the given game tick.
     */
    @Override
    public void tick(int tick) {
        y--; // Bullets always move upward
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.<br>
     * <p>
     * The text representation is "🔺".<br>
     * The image path is "assets/bullet.png".<br>
     *
     * @return the appropriate new ObjectGraphic.
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("🔺", "assets/bullet.png"); // Emoji representation for the bullet
    }
}
