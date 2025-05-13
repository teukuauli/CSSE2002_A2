package game;

import game.core.*;
import game.utility.Logger;
import game.achievements.PlayerStatsTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game information and state. Stores and manipulates the game state.
 */
public class GameModel {
    public static final int GAME_HEIGHT = 20;
    public static final int GAME_WIDTH = 10;
    public static final int START_SPAWN_RATE = 2; // spawn rate (percentage chance per tick)
    public static final int SPAWN_RATE_INCREASE = 5; // Increase spawn rate by 5% per level
    public static final int START_LEVEL = 1; // Starting level value
    public static final int SCORE_THRESHOLD = 100; // Score threshold for leveling
    public static final int ASTEROID_DAMAGE = 10; // The amount of damage an asteroid deals
    public static final int ENEMY_DAMAGE = 20; // The amount of damage an enemy deals
    public static final double ENEMY_SPAWN_RATE = 0.5; // Percentage of asteroid spawn chance
    public static final double POWER_UP_SPAWN_RATE = 0.25; // Percentage of asteroid spawn chance

    public final Random random = new Random(); // ONLY USED IN this.spawnObjects()
    private final List<SpaceObject> spaceObjects; // List of all objects
    private Ship ship; // Changed from boat to ship
    private int level; // Changed from lvl to level
    private int spawnRate; // The current game spawn rate
    private Logger logger; // Changed from wrter to logger
    private PlayerStatsTracker statsTracker; // Added statsTracker field
    private boolean isVerbose = false; // Added isVerbose field

    /**
     * Models a game, storing and modifying data relevant to the game.
     * @param logger a functional interface for passing information between classes.
     * @param statsTracker a PlayerStatsTracker instance to record stats.
     */
    public GameModel(Logger logger, PlayerStatsTracker statsTracker) {
        this.spaceObjects = new ArrayList<>();
        this.level = START_LEVEL;
        this.spawnRate = START_SPAWN_RATE;
        this.ship = new Ship();
        this.logger = logger;
        this.statsTracker = statsTracker;
    }

    /**
     * Returns the ship instance in the game.
     * @return the current ship instance.
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns a list of all SpaceObjects in the game.
     * @return a list of all spaceObjects.
     */
    public List<SpaceObject> getSpaceObjects() {
        return spaceObjects;
    }

    /**
     * Returns the current level.
     * @return the current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the current player stats tracker.
     * @return the current player stats tracker.
     */
    public PlayerStatsTracker getStatsTracker() {
        return statsTracker;
    }

    /**
     * Sets verbose state to the provided input.
     * @param verbose whether to set verbose state to true or false.
     */
    public void setVerbose(boolean verbose) {
        this.isVerbose = verbose;
    }

    /**
     * Adds a SpaceObject to the game.
     * @param object the SpaceObject to be added to the game.
     */
    public void addObject(SpaceObject object) {
        this.spaceObjects.add(object);
    }

    /**
     * Moves all objects and updates the game state.
     * @param tick the tick value passed through to the objects tick() method.
     */
    public void updateGame(int tick) {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            obj.tick(tick); // Move objects
            if (!isInBounds(obj)) { // Use isInBounds method
                toRemove.add(obj);
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Spawns new objects (Asteroids, Enemies, and PowerUp) at random positions.
     */
    public void spawnObjects() {
        // Spawn asteroids with a chance determined by spawnRate
        if (random.nextInt(100) < spawnRate) {
            int x = random.nextInt(GAME_WIDTH); // Random x-coordinate
            int y = 0; // Spawn at the top of the screen
            if (!isPositionOccupied(x, y)) {
                spaceObjects.add(new Asteroid(x, y));
            }
        }

        // Spawn enemies with a lower chance
        if (random.nextInt(100) < spawnRate * ENEMY_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            if (!isPositionOccupied(x, y)) {
                spaceObjects.add(new Enemy(x, y));
            }
        }

        // Spawn power-ups with an even lower chance
        if (random.nextInt(100) < spawnRate * POWER_UP_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            PowerUp powerUp = random.nextBoolean() ?
                    new ShieldPowerUp(x, y) : new HealthPowerUp(x, y);
            if (!isPositionOccupied(x, y)) {
                spaceObjects.add(powerUp);
            }
        }
    }

    /**
     * Checks if a given position would collide with the ship or any object.
     */
    private boolean isPositionOccupied(int x, int y) {
        if (ship.getX() == x && ship.getY() == y) {
            return true;
        }
        for (SpaceObject obj : spaceObjects) {
            if (obj.getX() == x && obj.getY() == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * If level progression requirements are satisfied, levels up the game.
     */
    public void levelUp() {
        if (ship.getScore() < level * SCORE_THRESHOLD) {
            return;
        }
        level++;
        spawnRate += SPAWN_RATE_INCREASE;
        if (isVerbose) { // Only log if verbose is true
            logger.log("Level Up! Welcome to Level " + level + ". Spawn rate increased to "
                    + spawnRate + "%.");
        }
    }

    /**
     * Fires a Bullet from the ship's current position.
     */
    public void fireBullet() {
        int bulletX = ship.getX();
        int bulletY = ship.getY();
        spaceObjects.add(new Bullet(bulletX, bulletY));
        if (isVerbose) {
            logger.log("Bullet fired!");
        }
    }

    /**
     * Detects and handles collisions between spaceObjects.
     */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();

        // Check Ship collisions
        for (SpaceObject obj : spaceObjects) {
            if (obj instanceof Bullet) {
                continue;
            }

            if (obj.getX() == ship.getX() && obj.getY() == ship.getY()) {
                if (obj instanceof PowerUp powerUp) {
                    powerUp.applyEffect(ship);
                    if (isVerbose) {
                        logger.log("PowerUp collected: " + obj.render());
                    }
                } else if (obj instanceof Asteroid) {
                    ship.takeDamage(ASTEROID_DAMAGE);
                    if (isVerbose) {
                        logger.log("Hit by asteroid! Health reduced by " + ASTEROID_DAMAGE + ".");
                    }
                } else if (obj instanceof Enemy) {
                    ship.takeDamage(ENEMY_DAMAGE);
                    if (isVerbose) {
                        logger.log("Hit by enemy! Health reduced by " + ENEMY_DAMAGE + ".");
                    }
                }
                toRemove.add(obj);
            }
        }

        // Check Bullet collisions
        for (SpaceObject bullet : spaceObjects) {
            if (!(bullet instanceof Bullet)) {
                continue;
            }

            for (SpaceObject target : spaceObjects) {
                if (target instanceof Enemy && bullet.getX() == target.getX() && bullet.getY() == target.getY()) {
                    toRemove.add(bullet);
                    toRemove.add(target);
                    statsTracker.recordShotHit(); // Record shot hit for Enemy hits
                    break;
                } else if (target instanceof Asteroid && bullet.getX() == target.getX() && bullet.getY() == target.getY()) {
                    toRemove.add(bullet);
                    break;
                }
            }
        }

        spaceObjects.removeAll(toRemove);
    }

    /**
     * Sets the seed of the Random instance created in the constructor using .setSeed().
     * @param seed to be set for the Random instance
     */
    public void setRandomSeed(int seed) {
        this.random.setSeed(seed);
    }

    /**
     * Checks if the game is over.
     * @return true if the Ship health is <= 0, false otherwise
     */
    public boolean checkGameOver() {
        return ship.getHealth() <= 0;
    }

    /**
     * Checks if the given SpaceObject is inside the game bounds.
     * @param spaceObject the SpaceObject to check
     * @return true if the SpaceObject is in bounds, false otherwise
     */
    public static boolean isInBounds(SpaceObject spaceObject) {
        return spaceObject.getX() >= 0 &&
                spaceObject.getX() < GAME_WIDTH &&
                spaceObject.getY() >= 0 &&
                spaceObject.getY() < GAME_HEIGHT;
    }
}