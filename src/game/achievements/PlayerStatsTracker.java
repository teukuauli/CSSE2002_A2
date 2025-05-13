package game.achievements;

/**
 * Represents a tracker for player statistics.
 * This class monitors the number and accuracy of shots the player has fired since the game start time.
 */
public class PlayerStatsTracker {

    private long startTime;
    private int shotsFired;
    private int shotsHit;

    /**
     * Constructs a PlayerStatsTracker with a custom start time.
     * @param startTime the time when the tracking began
     */
    public PlayerStatsTracker(long startTime) {
        this.startTime = startTime;
        this.shotsFired = 0;
        this.shotsHit = 0;
    }

    /**
     * Constructs a PlayerStatsTracker with current system time (in milliseconds) as the start time.
     */
    public PlayerStatsTracker() {
        this(System.currentTimeMillis());
    }

    /**
     * Records the player firing one shot, by incrementing shots fired by 1.
     */
    public void recordShotFired() {
        shotsFired++;
    }

    /**
     * Records the player hitting one target, by incrementing shots hit by 1.
     */
    public void recordShotHit() {
        shotsHit++;
    }

    /**
     * Returns the total number of shots that the player has fired.
     * @return the number of shots fired
     */
    public int getShotsFired() {
        return shotsFired;
    }

    /**
     * Returns the total number of shots the player has successfully hit.
     * @return the number of shots hit
     */
    public int getShotsHit() {
        return shotsHit;
    }

    /**
     * Returns the number of seconds elapsed since the tracker started.
     * @return the elapsed time in seconds
     */
    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    /**
     * Returns the player's shooting accuracy percentage.
     * Accuracy is calculated as shots hit divided by shots fired. Unless no shots are fired, in which case accuracy is 0.0.
     * @return the shooting average percentage as a decimal.
     */
    public double getAccuracy() {
        if (shotsFired == 0) {
            return 0.0;
        }
        return (double) shotsHit / shotsFired;
    }
}