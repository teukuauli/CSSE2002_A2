package game.achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GameAchievementManager coordinates achievement updates, file persistence management.
 * Responsibilities:
 * - Register new achievements.
 * - Update achievement progress.
 * - Check for Mastered achievements and log them using AchievementFile.
 * - Provide access to the current list of achievements.
 */
public class AchievementManager {

    private AchievementFile achievementFile;
    private Map<String, Achievement> achievements;
    private Map<String, Boolean> loggedStatus;

    /**
     * Constructs a GameAchievementManager with the specified AchievementFile.
     * @param achievementFile the AchievementFile instance to use (non-null)
     * @throws IllegalArgumentException if achievementFile is null.
     * @requires achievementFile is not null
     */
    public AchievementManager(AchievementFile achievementFile) {
        if (achievementFile == null) {
            throw new IllegalArgumentException("AchievementFile cannot be null");
        }
        this.achievementFile = achievementFile;
        this.achievements = new HashMap<>();
        this.loggedStatus = new HashMap<>();
    }

    /**
     * Registers a new achievement.
     * @param achievement the Achievement to register.
     * @throws IllegalArgumentException if achievement is already registered.
     * @requires achievement is not null
     */
    public void addAchievement(Achievement achievement) {
        if (achievement == null) {
            throw new IllegalArgumentException("Achievement cannot be null");
        }

        String achievementName = achievement.getName();
        if (achievements.containsKey(achievementName)) {
            throw new IllegalArgumentException("Achievement already registered: " + achievementName);
        }

        achievements.put(achievementName, achievement);
        loggedStatus.put(achievementName, false);
    }

    /**
     * Sets the progress of the specified achievement to a given amount.
     * @param achievementName the name of the achievement.
     * @param absoluteProgressValue the value the achievement's progress will be set to.
     * @throws IllegalArgumentException if no achievement is registered under the provided name.
     * @requires achievementName must be a non-null, non-empty string identifying a registered achievement.
     */
    public void updateAchievement(String achievementName, double absoluteProgressValue) {
        if (achievementName == null || achievementName.isEmpty()) {
            throw new IllegalArgumentException("Achievement name cannot be null or empty");
        }

        Achievement achievement = achievements.get(achievementName);
        if (achievement == null) {
            throw new IllegalArgumentException("No achievement registered with name: " + achievementName);
        }

        achievement.setProgress(absoluteProgressValue);
    }

    /**
     * Checks all registered achievements. For any achievement that is mastered and has not yet been logged,
     * this method logs the event via AchievementFile, and marks the achievement as logged.
     */
    public void logAchievementMastered() {
        for (Map.Entry<String, Achievement> entry : achievements.entrySet()) {
            String achievementName = entry.getKey();
            Achievement achievement = entry.getValue();

            if ("Master".equals(achievement.getCurrentTier()) && !loggedStatus.get(achievementName)) {
                String logData = String.format("%s - Mastered", achievementName);
                achievementFile.save(logData);
                loggedStatus.put(achievementName, true);
            }
        }
    }

    /**
     * Returns a list of all registered achievements.
     * @return a List of Achievement objects.
     */
    public List<Achievement> getAchievements() {
        return new ArrayList<>(achievements.values());
    }
}