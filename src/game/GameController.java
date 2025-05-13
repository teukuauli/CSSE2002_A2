package game;

import game.achievements.Achievement;
import game.achievements.AchievementManager;
import game.achievements.GameAchievement;
import game.achievements.PlayerStatsTracker;
import game.utility.Direction;
import game.ui.UI;
import game.core.*;

import java.util.List;
import java.util.ArrayList;

/**
 * The Controller handling the game flow and interactions.
 * <p>
 * Holds references to the UI and the Model, so it can pass information and references back and forth
 * as necessary.<br>
 * Manages changes to the game, which are stored in the Model, and displayed by the UI.<br>
 */
public class GameController {
    private final long startTime;
    private final UI ui;
    private final GameModel model;
    private final AchievementManager achievementManager;
    private boolean isPaused = false;

    /**
     * An internal variable indicating whether certain methods should log their actions.
     * Not all methods respect isVerbose.
     */
    private boolean isVerbose = false;

    /**
     * Initializes the game controller with the given UI, GameModel and AchievementManager.<br>
     * Stores the UI, GameModel, AchievementManager and start time.<br>
     * The start time System.currentTimeMillis() should be stored as a long.<br>
     * Starts the UI using UI.start().<br>
     *
     * @param ui the UI used to draw the Game
     * @param model the model used to maintain game information
     * @param achievementManager the manager used to maintain achievement information
     *
     * @requires ui is not null
     * @requires model is not null
     * @requires achievementManager is not null
     * @provided
     */
    public GameController(UI ui, GameModel model, AchievementManager achievementManager) {
        if (ui == null) {
            throw new IllegalArgumentException("UI cannot be null");
        }
        if (model == null) {
            throw new IllegalArgumentException("GameModel cannot be null");
        }
        if (achievementManager == null) {
            throw new IllegalArgumentException("AchievementManager cannot be null");
        }

        this.ui = ui;
        ui.start();
        this.model = model;
        this.startTime = System.currentTimeMillis();
        this.achievementManager = achievementManager;

        // Check if achievements are already registered before adding them
        if (achievementManager.getAchievements().isEmpty()) {
            achievementManager.addAchievement(new GameAchievement("Survivor", 
                    "Survive for 120 seconds"));
            achievementManager.addAchievement(new GameAchievement("Enemy Exterminator", 
                    "Hit 20 shots"));
            achievementManager.addAchievement(new GameAchievement("Sharp Shooter", 
                    "Achieve 99% accuracy"));
        }
    }

    /**
     * Initializes the game controller with the given UI and GameModel.<br>
     * Stores the ui, model and start time.<br>
     * The start time System.currentTimeMillis() should be stored as a long.<br>
     *
     * @param ui    the UI used to draw the Game
     * @param achievementManager the manager used to maintain achievement information
     *
     * @requires ui is not null
     * @requires achievementManager is not null
     * @provided
     */
    public GameController(UI ui, AchievementManager achievementManager) {
        this(ui, new GameModel(ui::log, new PlayerStatsTracker()), achievementManager);
    }

    /**
     * Returns the current GameModel.
     * @return the current GameModel.
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * Returns the current PlayerStatsTracker.
     * @return the current PlayerStatsTracker
     */
    public PlayerStatsTracker getStatsTracker() {
        return model.getStatsTracker();
    }

    /**
     * Sets verbose state to the provided input. Also sets the model's verbose state to the provided
     * input.
     * @param verbose whether to set verbose state to true or false.
     */
    public void setVerbose(boolean verbose) {
        this.isVerbose = verbose;
        model.setVerbose(verbose);
    }

    /**
     * Starts the main game loop.<br>
     * <p>
     * Passes onTick and handlePlayerInput to ui.onStep and ui.onKey respectively.
     * @provided
     */
    public void startGame() {
        ui.onStep(this::onTick);
        ui.onKey(this::handlePlayerInput);
    }

    /**
     * Uses the provided tick to call and advance the following:<br>
     * - A call to model.updateGame(tick) to advance the game by the given tick.<br>
     * - A call to model.checkCollisions() to handle game interactions.<br>
     * - A call to model.spawnObjects() to handle object creation.<br>
     * - A call to model.levelUp() to check and handle leveling.<br>
     * - A call to refreshAchievements(tick) to handle achievement updating.<br>
     * - A call to renderGame() to draw the current state of the game.<br>
     * @param tick the provided tick
     * @provided
     */
    public void onTick(int tick) {
        model.updateGame(tick);
        model.checkCollisions();
        model.spawnObjects();
        model.levelUp();
        refreshAchievements(tick);
        renderGame();

        // Check game over
        if (model.checkGameOver()) {
            pauseGame();
            showGameOverWindow();
        }
    }

    /**
     * Updates the player's progress towards achievements on every game tick, and uses the
     * achievementManager to track and update the player's achievements.
     * Progress is a double representing completion percentage, and must be >= 0.0, and <= 1.0.
     *
     * Achievement Progress Calculations:
     * - Survivor achievement: survival time since game start in seconds, mastered at 120 seconds.
     * - Enemy Exterminator achievement: shots hit since game start, mastered at 20 shots.
     * - Sharp Shooter achievement: if shots fired > 10, then result is accuracy / 0.99, with the
     *   maximum result possible being 1; otherwise if shots fired <= 10, result is 0.
     * (This is so that mastery is achieved at accuracy >= 0.99)
     *
     * The AchievementManager stores all new achievements mastered, and then updates the UI
     * statistics with each new achievement's name and progress value.
     * Once every 100 ticks, and only if verbose is true, the achievement progress is logged to the
     * UI.
     *
     * @param tick the provided tick
     */
    public void refreshAchievements(int tick) {
        // Calculate progress for each achievement
        PlayerStatsTracker statsTracker = getStatsTracker();
        long elapsedSeconds = statsTracker.getElapsedSeconds();
        int shotsHit = statsTracker.getShotsHit();
        int shotsFired = statsTracker.getShotsFired();
        double accuracy = statsTracker.getAccuracy();

        // Update achievement progress according to specifications

        // Survivor achievement: progress = elapsedSeconds / 120, capped at 1.0
        double survivorProgress = Math.min(1.0, elapsedSeconds / 120.0);
        achievementManager.updateAchievement("Survivor", survivorProgress);

        // Enemy Exterminator achievement: progress = shotsHit / 20, capped at 1.0
        double exterminatorProgress = Math.min(1.0, shotsHit / 20.0);
        achievementManager.updateAchievement("Enemy Exterminator", exterminatorProgress);

        // Sharp Shooter achievement: if shotsFired > 10, progress = accuracy / 0.99 capped at 1.0
        double sharpShooterProgress = 0.0;
        if (shotsFired > 10) {
            sharpShooterProgress = Math.min(1.0, accuracy / 0.99);
        }
        achievementManager.updateAchievement("Sharp Shooter", sharpShooterProgress);

        // Check and log mastered achievements
        achievementManager.logAchievementMastered();

        // Update UI with achievement progress stats
        for (Achievement achievement : achievementManager.getAchievements()) {
            ui.setAchievementProgressStat(achievement.getName(), achievement.getProgress());
        }

        // Log achievements every 100 ticks if verbose
        if (isVerbose && tick % 100 == 0) {
            ui.logAchievements(achievementManager.getAchievements());
        }
    }

    /**
     * Renders the current game state, including score, health, level, and survival time.
     * - Uses ui.setStat() to update the "Score", "Health" and "Level" appropriately with
     *   information from the model.
     * - Uses ui.setStat() to update "Time Survived" with (System.currentTimeMillis() - startTime)
     *   / 1000 + " seconds"
     * - Renders all spaceObjects using one call to ui.render().
     */
    public void renderGame() {
        // Update statistics - convert to strings for UI.setStat
        ui.setStat("Score", String.valueOf(model.getShip().getScore()));
        ui.setStat("Health", String.valueOf(model.getShip().getHealth()));
        ui.setStat("Level", String.valueOf(model.getLevel()));
        ui.setStat("Time Survived", 
                String.valueOf((System.currentTimeMillis() - startTime) / 1000) + " seconds");

        // Create a single list with all objects in the correct order
        List<SpaceObject> allObjects = new ArrayList<>();
        allObjects.add(model.getShip()); // Add ship first for proper layering
        allObjects.addAll(model.getSpaceObjects()); // Add all other objects

        // Render all objects with a single call
        ui.render(allObjects);
    }

    /**
     * Handles player input and performs actions such as moving the ship or firing Bullets.
     * Uppercase and lowercase inputs should be treated identically:
     * - For movement keys "W", "A", "S" and "D" the ship should be moved up, left, down, or right
     *   respectively, unless the game is paused. The movement should also be logged, provided
     *   verbose is true.
     * - For input "F" the fireBullet() method of the Model instance should be called,
     *   and the recordShotFired() method of the PlayerStatsTracker instance should be called.
     * - For input "P" the pauseGame() method should be called.
     * - For all other inputs, "Invalid input. Use W, A, S, D, F, or P." should be logged.
     * When the game is paused, only un-pausing should be possible.
     *
     * @param input the player's input command.
     */
    public void handlePlayerInput(String input) {
        if (isPaused && !input.equalsIgnoreCase("P")) {
            return; // Only unpausing allowed while paused
        }

        String upperInput = input.toUpperCase();

        try {
            switch (upperInput) {
                case "W":
                    handleMovement(Direction.UP);
                    break;
                case "A":
                    handleMovement(Direction.LEFT);
                    break;
                case "S":
                    handleMovement(Direction.DOWN);
                    break;
                case "D":
                    handleMovement(Direction.RIGHT);
                    break;
                case "F":
                    model.fireBullet();
                    getStatsTracker().recordShotFired();
                    break;
                case "P":
                    pauseGame();
                    break;
                default:
                    ui.log("Invalid input. Use W, A, S, D, F, or P.");
                    break;
            }
        } catch (Exception e) {
            ui.log(e.getMessage());
        }
    }

    /**
     * Helper method to handle ship movement and verbose logging
     *
     * @param direction the direction to move the ship
     */
    private void handleMovement(Direction direction) {
        model.getShip().move(direction);
        if (isVerbose) {
            ui.log(String.format("Ship moved to (%d, %d)",
                    model.getShip().getX(), model.getShip().getY()));
        }
    }

    /**
     * Calls ui.pause() to pause the game until the method is called again.
     * Logs "Game paused." or "Game unpaused." as appropriate, after calling ui.pause(),
     * irrespective of verbose state.
     */
    public void pauseGame() {
        isPaused = !isPaused;
        ui.pause();
        if (isPaused) {
            ui.log("Game paused.");
        } else {
            ui.log("Game unpaused.");
        }
    }

    /**
     * Displays a Game Over window containing the player's final statistics and achievement
     * progress.
     */
    private void showGameOverWindow() {
        // Create a new window to display game over stats.
        javax.swing.JFrame gameOverFrame = new javax.swing.JFrame("Game Over - Player Stats");
        gameOverFrame.setSize(400, 300);
        gameOverFrame.setLocationRelativeTo(null); // center on screen
        gameOverFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        StringBuilder sb = new StringBuilder();
        PlayerStatsTracker statsTracker = getStatsTracker();
        sb.append("Shots Fired: ").append(statsTracker.getShotsFired()).append("\n");
        sb.append("Shots Hit: ").append(statsTracker.getShotsHit()).append("\n");
        sb.append("Enemies Destroyed: ").append(statsTracker.getShotsHit()).append("\n");
        sb.append("Survival Time: ").append(statsTracker.getElapsedSeconds())
                .append(" seconds\n");

        List<Achievement> achievements = achievementManager.getAchievements();
        sb.append("\n=== Achievements ===\n");
        for (Achievement ach : achievements) {
            double progressPercent = ach.getProgress() * 100;
            sb.append(ach.getName())
                    .append(" - ")
                    .append(ach.getDescription())
                    .append(" (")
                    .append(String.format("%.0f%%", progressPercent))
                    .append(" complete, Tier: ")
                    .append(ach.getCurrentTier())
                    .append(")\n");
        }

        String statsText = sb.toString();

        // Create a text area to show stats.
        javax.swing.JTextArea statsArea = new javax.swing.JTextArea(statsText);
        statsArea.setEditable(false);
        statsArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));

        // Add the text area to a scroll pane and add it to the frame.
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(statsArea);
        gameOverFrame.add(scrollPane);

        // Make the window visible.
        gameOverFrame.setVisible(true);
    }
}
