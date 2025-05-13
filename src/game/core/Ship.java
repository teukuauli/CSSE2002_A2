package game.core;

import game.ui.ObjectGraphic;

/**
 * Represents the player's ship.
 */
public class Ship extends Controllable {
    private static final int STARTING_HEALTH = 100;
    private static final int STARTING_SCORE = 0;
    private static final int STARTING_X = 5;
    private static final int STARTING_Y = 10;
    private int HEALTH;


    private int score;

    /**
     * Constructs a Ship with the specified position and health.
     * Also initialises score to be 0.
     *
     * @param x      the initial x coordinate.
     * @param y      the initial y coordinate.
     * @param health the initial health of the ship.
     */
    public Ship(int x, int y, int health) {
        super(x, y);
        this.HEALTH = health;
        this.score = STARTING_SCORE;
    }

    /**
     * Constructs a Ship with default position and health.<br>
     * <p>
     * By default, a ship should be at position x = 5 and y = 10, with 100 points of health.<br>
     */
    public Ship() {
        this(STARTING_X, STARTING_Y, STARTING_HEALTH);
    }

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.<br>
     * <p>
     * The text representation is "🚀".<br>
     * The image path is "assets/ship.png".<br>
     *
     * @return the appropriate new ObjectGraphic.
     */
    @Override
    public ObjectGraphic render() {
        return new ObjectGraphic("🚀", "assets/ship.png"); // Emoji representation for the ship
    }

    /**
     * Reduces the ship's health by the specified damage amount.<br>
     * A ship's health can never fall below 0.<br>
     *
     * @param damage the amount of damage taken.
     */
    public void takeDamage(int damage) {
        HEALTH -= damage;
        if (HEALTH < 0) {
            HEALTH = 0; // Prevent health from dropping below 0
        }
    }

    /**
     * Heals the ship by the specified amount.<br>
     * A ship's health can never rise above 100.<br>
     *
     * @param num the amount of health restored.
     */
    public void heal(int num) {
        HEALTH += num;
        if (HEALTH > 100) {
            HEALTH = 100; // Cap health at 100
        }
    }

    /**
     * Adds points to the ship's score.
     *
     * @param points the points to add.
     */
    public void addScore(int points) {
        score += points;
    }

    /**
     * Returns the current health of the ship.
     *
     * @return the current health.
     */
    public int getHealth() {
        return HEALTH;
    }

    /**
     * Returns the current score of the ship.
     *
     * @return the current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * As Ships have no tick-dependent behaviour, this method should be left blank.<br>
     *
     * @param tick the given game tick.
     */
    @Override
    public void tick(int tick) {

    }
}
