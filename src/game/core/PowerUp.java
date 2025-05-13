package game.core;

/**
 * Represents a power-up in the game.
 */
public abstract class PowerUp extends ObjectWithPosition implements PowerUpEffect {

    /**
     * Creates a new PowerUp with the given coordinate.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public PowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * As PowerUps have no tick-dependent behaviour, this method should be left blank.<br>
     *
     * @param tick the given game tick.
     */
    @Override
    public void tick(int tick) {

    }
}
