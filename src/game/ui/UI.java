package game.ui;

import game.achievements.Achievement;

import game.core.SpaceObject;

import java.util.List;

/**
 * Represents a usable User-Interface.
 */
public interface UI {

    /**
     * Starts the UI.
     */
    void start();

    /**
     * Alternates between paused and unpaused state.
     */
    void pause();

    /**
     * Pauses the game
     */
    void stop();

    /**
     * Stores the provided tickable.
     * @param tickable the provided tickable
     */
    void onStep(Tickable tickable);

    /**
     * Stores the provided KeyHandler.
     * @param key the provided KeyHandler
     */
    void onKey(KeyHandler key);

    /**
     * Renders the provided objects, and refreshes the UI.
     *
     * @param objects the list of SpaceObjects to be rendered.
     */
    void render(List<SpaceObject> objects);

    /**
     * Log the provided message.
     *
     * @param message the provided message
     */
    void log(String message);

    /**
     * Sets the stats to the provided label and value.
     *
     * @param label the provided label
     * @param value the provided value
     */
    void setStat(String label, String value);

    /**
     * Logs a message to the UI when an achievement is mastered.<br>
     *
     * @param message the message to be logged
     */
    void logAchievementMastered(String message);

    /**
     * Logs the current progress of all achievements to the UI.<br>
     *
     * @param achievements  the achievements to be logged
     */
    void logAchievements(List<Achievement> achievements);

    /**
     * Updates the name and progress percentage for each achievement.<br>
     *
     * @param achievementName  the name of the achievement
     * @param progressPercentage the progress as a decimal value
     */
    void setAchievementProgressStat(String achievementName, double progressPercentage);

}
