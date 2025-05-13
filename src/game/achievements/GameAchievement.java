package game.achievements;

/**
 * A concrete implementation of the Achievement interface.
 * Sample logic:
 * - Progress is tracked as a value between 0.0 and 1.0.
 * - Tiers are determined as:
 *   - "Novice" if progress < 0.5,
 *   - "Expert" if progress is between 0.5 (inclusive) and 0.999 (exclusive),
 *   - "Master" if progress is 0.999 or greater.
 */
public class GameAchievement implements Achievement {

    private String name;
    private String description;
    private double progress;

    /**
     * Constructs an Achievement with the specified name and description. The initial progress is 0.
     * @param name the unique name.
     * @param description the achievement description.
     * @requires name is not null.
     * @requires name is not empty.
     * @requires description is not null.
     * @requires description is not empty.
     */
    public GameAchievement(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        this.name = name;
        this.description = description;
        this.progress = 0.0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public void setProgress(double newProgress) {
        if (newProgress < 0.0 || newProgress > 1.0) {
            throw new IllegalArgumentException("Progress must be between 0.0 and 1.0, inclusive");
        }
        this.progress = newProgress;
    }

    @Override
    public String getCurrentTier() {
        if (progress < 0.5) {
            return "Novice";
        } else if (progress < 1.0) {
            return "Expert";
        } else {
            return "Master";
        }
    }
}