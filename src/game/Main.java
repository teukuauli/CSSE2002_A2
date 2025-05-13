package game;

import game.achievements.*;
import game.ui.gui.GUI;

/**
 * Entry point for the Space Shooter game.
 * @provided
 */
public class Main {
    /**
     * The game entry point.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        GameController gameController = new GameController(new GUI(), getAchievementManager());
        gameController.startGame();
    }

    /**
     * Initialises and returns a new AchievementManager containing standard
     * game achievements.
     *
     * @return an initialised AchievementManager containing standard achievements
     */
    private static AchievementManager getAchievementManager() {
        AchievementManager achievementManager = new AchievementManager(new FileHandler());

        addStandardAchievements(achievementManager);
        return achievementManager;
    }

    /**
     * Adds the default set of game achievements to the AchievementManager during game
     * initialisation.
     *
     * @param achievementManager the AchievementManager storing the name and descriptions for each
     *                           default achievement
     * @requires achievementManager is not null
     */
    private static void addStandardAchievements(AchievementManager achievementManager) {
        achievementManager.addAchievement(new GameAchievement(
                "Enemy Exterminator",
                "Destroy enough enemy ships to unlock."
        ));
        achievementManager.addAchievement(new GameAchievement(
                "Survivor",
                "Stay alive for increasing durations to unlock."
        ));
        achievementManager.addAchievement(new GameAchievement(
                "Sharp Shooter",
                "Improve your shooting accuracy to unlock."
        ));
    }
}
