package game.core;

/**
 * Represents the effect of a power-up in the game.
 */
public interface PowerUpEffect {
    /**
     * Applies the power-up's effect to the specified ship.
     *
     * @param ship the ship to apply the effect to.
     */
    void applyEffect(Ship ship);
}
