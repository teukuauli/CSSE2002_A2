package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents a shield power-up in the game.
 */
public class ShieldPowerUp extends PowerUp {

    /**
     * Creates a new ShieldPowerUp with the given coordinates.
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public ShieldPowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.<br>
     * <p>
     * The text representation is "💠".<br>
     * The image path is "assets/shield.png".<br>
     *
     * @return the appropriate new ObjectGraphic.
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("💠", "assets/shield.png"); // Emoji representation for the shield
    }

    /**
     * Applies the shield effect to the ship, increasing the score by 50.<br>
     * Sends "Shield activated! Score increased by 50." to standard output (using System.out.println()).<br>
     *
     * @param ship the ship to apply the effect to.
     */
    @Override
    public void applyEffect(Ship ship) {
        ship.addScore(50);
        System.out.println("Shield activated! Score increased by 50.");
    }
}
