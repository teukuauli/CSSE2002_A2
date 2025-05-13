package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents a health power-up in the game.
 */
public class HealthPowerUp extends PowerUp {

    /**
     * Creates a health power-up at the given coordinates
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public HealthPowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.<br>
     * <p>
     * The text representation is "❤️".<br>
     * The image path is "assets/health.png".<br>
     *
     * @return the appropriate new ObjectGraphic.
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("❤️", "assets/health.png");
    }

    /**
     * Applies the health effect to the ship, healing it for 20 health.<br>
     * Sends "Health restored by 20!" to standard output (using System.out.println()).<br>
     *
     * @param ship the ship to apply the effect to.
     */
    @Override
    public void applyEffect(Ship ship) {
        ship.heal(20);
        System.out.println("Health restored by 20!");
    }
}
