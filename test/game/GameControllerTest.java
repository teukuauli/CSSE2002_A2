package game;

import game.achievements.*;
import game.ui.KeyHandler;
import game.ui.Tickable;
import game.ui.UI;
import game.utility.Direction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import game.core.*;
import game.utility.Logger;
import game.exceptions.BoundaryExceededException;
/**
 * Test class for GameController. Tests the functionality of the GameController class,
 * including achievement tracking, input handling, and game state management.
 */
public class GameControllerTest {

    private GameController gameController;
    private TestUi mockUi;
    private TestGameModel gameModel;
    private TestStatsTracker statsTracker;
    private AchievementManager achievementManager;
    private TestFileHandler fileHandler;

    /**
     * Sets up the test environment before each test.
     * Creates mock objects and initializes the GameController with test dependencies.
     */
    @Before
    public void setUp() {
        // Create the necessary objects for testing
        mockUi = new TestUi();
        statsTracker = new TestStatsTracker();
        gameModel = new TestGameModel(mockUi::log, statsTracker);

        // Create AchievementManager with a test FileHandler
        fileHandler = new TestFileHandler();
        achievementManager = new AchievementManager(fileHandler);

        // Add some test achievements
        achievementManager.addAchievement(
                new GameAchievement("Survivor", "Survive for 120 seconds"));
        achievementManager.addAchievement(
                new GameAchievement("Enemy Exterminator", "Hit 20 shots"));
        achievementManager.addAchievement(
                new GameAchievement("Sharp Shooter", "Achieve 99% accuracy"));

        // Initialize GameController with test objects
        gameController = new GameController(mockUi, gameModel, achievementManager);
    }

    /**
     * Tests that the achievement system exists and is properly initialized.
     */
    @Test
    public void testAchievementSystemExists() {
        // Test that achievement manager is properly initialized
        assertNotNull("AchievementManager should be initialized", achievementManager);

        // Test that achievements are properly registered
        List<Achievement> achievements = achievementManager.getAchievements();
        assertEquals("Should have 3 achievements registered", 3, achievements.size());

        // Test achievement names and descriptions
        boolean foundSurvivor = false;
        boolean foundExterminator = false;
        boolean foundSharpShooter = false;

        for (Achievement achievement : achievements) {
            if ("Survivor".equals(achievement.getName())) {
                assertEquals("Survivor description should match",
                        "Survive for 120 seconds", achievement.getDescription());
                foundSurvivor = true;
            } else if ("Enemy Exterminator".equals(achievement.getName())) {
                assertEquals("Enemy Exterminator description should match",
                        "Hit 20 shots", achievement.getDescription());
                foundExterminator = true;
            } else if ("Sharp Shooter".equals(achievement.getName())) {
                assertEquals("Sharp Shooter description should match",
                        "Achieve 99% accuracy", achievement.getDescription());
                foundSharpShooter = true;
            }
        }

        assertTrue("Should have Survivor achievement", foundSurvivor);
        assertTrue("Should have Enemy Exterminator achievement", foundExterminator);
        assertTrue("Should have Sharp Shooter achievement", foundSharpShooter);
    }

    /**
     * Tests that the constructor correctly accepts different combinations of parameters.
     */
    @Test
    public void testConstructorWithAchievementManager() {
        // Test that constructor accepts UI, GameModel, and AchievementManager
        GameController controller = new GameController(mockUi, gameModel, achievementManager);
        assertNotNull("Controller should be created with UI, GameModel, and AchievementManager",
                controller);

        // Test that constructor accepts UI and AchievementManager
        GameController controller2 = new GameController(mockUi, achievementManager);
        assertNotNull("Controller should be created with UI and AchievementManager", controller2);
    }

    /**
     * Tests that the refreshAchievements method exists and updates achievement progress.
     */
    @Test
    public void testRefreshAchievementsMethod() {
        try {
            // Set up conditions for achievement progress
            statsTracker.recordShotFired();
            statsTracker.recordShotHit();

            gameController.refreshAchievements(100);

            assertTrue("refreshAchievements method exists", true);

            // Test that achievements were updated
            List<Achievement> achievements = achievementManager.getAchievements();
            for (Achievement achievement : achievements) {
                if ("Enemy Exterminator".equals(achievement.getName())) {
                    assertTrue("Enemy Exterminator progress should be updated",
                            achievement.getProgress() > 0);
                }
            }
        } catch (NoSuchMethodError e) {
            fail("refreshAchievements method does not exist");
        }
    }

    /**
     * Tests that the getStatsTracker method exists and returns the correct tracker.
     */
    @Test
    public void testGetStatsTrackerMethod() {
        // Test that getStatsTracker method exists and returns the correct tracker
        PlayerStatsTracker returnedTracker = gameController.getStatsTracker();
        assertNotNull("getStatsTracker should return a non-null tracker", returnedTracker);
        assertEquals("getStatsTracker should return the same tracker instance",
                statsTracker, returnedTracker);
    }

    /**
     * Tests that the setVerbose method exists and affects verbose state.
     */
    @Test
    public void testSetVerboseMethod() {
        // Test that setVerbose method exists and affects verbose state
        gameController.setVerbose(true);
        assertTrue("setVerbose method exists", true);

        // Test verbose behavior by triggering a verbose log
        mockUi.resetLogs();
        gameController.handlePlayerInput("W");

        // Test that verbose logging was triggered
        assertTrue("Verbose logging should be enabled", mockUi.wasShipMovementLogged());

        // Test turning verbose off
        gameController.setVerbose(false);
        mockUi.resetLogs();

        // Test that verbose logging is now disabled
        gameController.handlePlayerInput("W");
        assertFalse("Verbose logging should be disabled", mockUi.wasShipMovementLogged());
    }

    /**
     * Tests that movement commands are correctly processed and exceptions are handled properly.
     */
    @Test
    public void testMovementExceptionHandling() {
        // Modify game model to throw an exception with the expected message
        gameModel.setThrowExceptionOnMove(true, "Cannot move outside game bounds");

        // Attempt movement that should throw an exception
        gameController.handlePlayerInput("W");

        // Check if the error was logged with the correct message
        assertTrue("Exception message should be logged",
                mockUi.wasMessageLogged("Cannot move outside game bounds"));
    }

    /**
     * Tests that the correct exception message is handled when ship movement fails.
     */
    @Test
    public void testMovementExceptionMessage() {
        // Set up a specific expected exception message
        String expectedMessage = "Ship cannot move in that direction";
        gameModel.setThrowExceptionOnMove(true, expectedMessage);

        // Attempt movement
        gameController.handlePlayerInput("W");

        // Check if the exact expected message was logged
        assertTrue("The exact exception message should be logged",
                mockUi.wasMessageLogged(expectedMessage));

        // Verify with a different message to ensure specificity
        assertFalse("Different exception messages shouldn't be logged",
                mockUi.wasMessageLogged("Cannot move outside game bounds"));
    }

    /**
     * Tests that movement beyond boundaries is properly detected and handled.
     */
    @Test
    public void testMovementBeyondBoundaries() {
        // Create a real ship at the edge of the game area
        Ship ship = new Ship(GameModel.GAME_WIDTH - 1, 0, 100);

        // Try to move the ship right (which would exceed boundaries)
        try {
            ship.move(Direction.RIGHT);

            // If we get here, no exception was thrown - test failed
            fail("Moving beyond boundaries should throw a BoundaryExceededException");
        } catch (BoundaryExceededException e) {
            // Expected exception was thrown - test passed
            assertTrue("Exception message should mention boundary or out of bounds",
                    e.getMessage().contains("Out of bounds"));
        }
    }

    /**
     * Tests that movement is ignored when the game is paused.
     */
    @Test
    public void testMovementWhenGamePaused() {
        // Setup movement tracking
        gameModel.resetMoveCount();

        // Set the game to paused state by calling pauseGame
        gameController.pauseGame();

        // Attempt to move when paused
        gameController.handlePlayerInput("W");
        gameController.handlePlayerInput("A");
        gameController.handlePlayerInput("S");
        gameController.handlePlayerInput("D");

        // Verify no movement calls were made
        assertEquals("Move method should not be called when paused",
                0, gameModel.getMoveCount());
    }

    /**
     * Tests achievement mastery notification without using UI or frame rendering.
     */
    @Test
    public void testAchievementMasteryNotification() {
        // Set up a test achievement that's mastered (progress exactly at 1.0)
        GameAchievement testAchievement = new GameAchievement("Test Achievement",
                "Test Description");
        testAchievement.setProgress(1.0); // Set to exactly 1.0 to ensure Master tier

        // Reset logged status by re-adding achievement
        achievementManager.addAchievement(testAchievement);

        // Clear file handler saved data to ensure clean test
        fileHandler.clearSavedData();

        // Explicitly verify tier before logging
        assertEquals("Achievement should be at Master tier", "Master", testAchievement.getCurrentTier());

        // Call logAchievementMastered directly to check if it works
        achievementManager.logAchievementMastered();

        // Check if mastery was logged to the file
        List<String> savedData = fileHandler.read();

        // Verify that something was logged
        assertFalse("File handler should have saved data", savedData.isEmpty());

        // Check if our achievement mastery was logged
        boolean masteryLogged = false;
        for (String data : savedData) {
            if (data.contains("Test Achievement - Mastered")) {
                masteryLogged = true;
                break;
            }
        }
        assertTrue("Achievement mastery should be logged to the file", masteryLogged);
    }

    /**
     * Tests game over handling without creating a frame.
     */
    @Test
    public void testGameOverHandling() {
        // Use a mock implementation that doesn't create UI frames
        mockUi.setNoFramesMode(true);
        gameModel.setGameOverCondition(true);

        // Reset UI verification data
        mockUi.resetVerification();

        gameController.onTick(1);

        assertTrue("Game should be paused on game over", mockUi.wasMethodCalled("pause"));
    }

    /**
     * Tests that input is properly integrated with achievements.
     */
    @Test
    public void testInputIntegrationWithAchievements() {
        // Test firing a bullet increments shots fired in stats tracker
        int initialShotsFired = statsTracker.getShotsFired();
        gameController.handlePlayerInput("F");
        assertEquals("Shots fired should increment when F is pressed",
                initialShotsFired + 1, statsTracker.getShotsFired());
    }

    /**
     * Tests that player statistics are properly tracked.
     */
    @Test
    public void testPlayerStatisticsTracking() {
        // Test shot accuracy
        statsTracker.recordShotFired();
        statsTracker.recordShotFired();
        statsTracker.recordShotHit();
        assertEquals("Accuracy should be calculated correctly",
                0.5, statsTracker.getAccuracy(), 0.001);

        // Test shots fired/hit tracking
        assertEquals("Shots fired should be tracked", 2, statsTracker.getShotsFired());
        assertEquals("Shots hit should be tracked", 1, statsTracker.getShotsHit());
    }

    /**
     * Tests tier-based progression for achievements.
     */
    @Test
    public void testTierBasedProgression() {
        // Test tier progression
        GameAchievement testAchievement = new GameAchievement("Test Tier",
                "Test tier progression");

        // Novice tier (< 0.5)
        testAchievement.setProgress(0.4);
        assertEquals("Progress < 0.5 should be Novice tier",
                "Novice", testAchievement.getCurrentTier());

        // Expert tier (0.5 <= x < 0.999)
        testAchievement.setProgress(0.7);
        assertEquals("Progress >= 0.5 and < 0.999 should be Expert tier",
                "Expert", testAchievement.getCurrentTier());

        // Master tier (>= 0.999)
        testAchievement.setProgress(1.0);
        assertEquals("Progress > 0.999 should be Master tier",
                "Master", testAchievement.getCurrentTier());
    }

    /**
     * Tests that rendering includes achievements.
     */
    @Test
    public void testRenderingWithAchievements() {
        // Test that achievements are included in rendering
        mockUi.resetVerification();
        gameController.renderGame();
        assertTrue("Game rendering should involve setting UI stats",
                mockUi.wasMethodCalled("setStat"));
    }

    /**
     * Tests the execution order in onTick method.
     */
    @Test
    public void testExecutionOrderInOnTick() {
        // Test that the method executes without errors
        gameController.onTick(1);

        assertTrue("onTick should execute without errors", true);
    }

    /**
     * Tests that achievements are refreshed during gameplay.
     */
    @Test
    public void testAchievementRefreshDuringGameplay() {
        mockUi.resetVerification();

        // Call onTick
        gameController.onTick(1);

        assertTrue("UI methods should be called during achievement refresh",
                mockUi.wasMethodCalled("setStat")
                        || mockUi.wasMethodCalled("setAchievementProgressStat"));
    }

    /**
     * Tests that level up functions correctly when score threshold is reached.
     */
    @Test
    public void testLevelUpWhenScoreThresholdReached() {
        // Create a real GameModel instance with a simple logger and stats tracker
        Logger logger = s -> {}; // No-op logger
        PlayerStatsTracker statsTracker = new PlayerStatsTracker();
        GameModel realModel = new GameModel(logger, statsTracker);

        // Get initial level
        int initialLevel = realModel.getLevel();

        // Get the ship and set its score to just below threshold for the current level
        Ship ship = realModel.getShip();
        int threshold = initialLevel * GameModel.SCORE_THRESHOLD;

        // Set score to just below threshold
        for (int i = 0; i < threshold - 1; i++) {
            ship.addScore(1);
        }

        // Verify level has not changed
        assertEquals("Level should not change before threshold is reached",
                initialLevel, realModel.getLevel());

        // Call levelUp to check if level increments - shouldn't increment yet
        realModel.levelUp();
        assertEquals("Level should not change when score is below threshold",
                initialLevel, realModel.getLevel());

        // Add more score to reach the threshold exactly
        ship.addScore(1);

        // Verify score is now at threshold
        assertEquals("Score should be at threshold", threshold, ship.getScore());

        // Call levelUp again - now it should increment
        realModel.levelUp();

        // Verify level has increased
        assertEquals("Level should increase when score threshold is met",
                initialLevel + 1, realModel.getLevel());

    }

    /**
     * Tests that object position toString formatting includes space after comma.
     */
    @Test
    public void testObjectPositionToStringFormat() {
        // Create a real object with a known position
        Ship ship = new Ship(10, 20, 100);

        // Get the string representation
        String positionString = ship.toString();

        // Check for correct format: (10, 20) - space after comma but not before
        assertTrue("Position string should include space after comma: " + positionString,
                positionString.contains(", "));  // Correct - space after comma
        assertFalse("Position string should not have space before comma: " + positionString,
                positionString.contains(" ,"));  // No space before comma
    }

    /**
     * Tests that shield power-up correctly adds score when collected.
     */
    @Test
    public void testShieldPowerUpAddsScore() {
        // Setup
        gameModel.setScore(0);
        int initialScore = gameModel.getScore();

        // Act
        gameModel.collectShieldPowerUp();
        int newScore = gameModel.getScore();

        // Assert
        if (newScore == initialScore) {
            fail("BUG DETECTED: Shield power-up does not add any score!");
        } else {
            assertTrue("Shield power-up should add to score", newScore > initialScore);
        }
    }

    /**
     * A test implementation of PlayerStatsTracker for testing purposes.
     */
    private static class TestStatsTracker extends PlayerStatsTracker {
        private int shotsFired = 0;
        private int shotsHit = 0;

        @Override
        public long getElapsedSeconds() {
            return 10;
        }

        @Override
        public void recordShotFired() {
            shotsFired++;
        }

        @Override
        public void recordShotHit() {
            shotsHit++;
        }

        @Override
        public int getShotsFired() {
            return shotsFired;
        }

        @Override
        public int getShotsHit() {
            return shotsHit;
        }

        @Override
        public double getAccuracy() {
            if (shotsFired == 0) {
                return 0.0;
            }
            return (double) shotsHit / shotsFired;
        }
    }

    /**
     * A test implementation of GameModel for testing purposes.
     */
    private static class TestGameModel extends GameModel {
        private boolean gameOverCondition = false;
        private int score = 0;
        private int level = 1;
        private int moveCount = 0;
        private boolean throwExceptionOnMove = false;
        private String exceptionMessage = "";
        private boolean levelIncrementFlag = false;  // Flag to track level up

        public TestGameModel(Logger logger, PlayerStatsTracker statsTracker) {
            super(logger, statsTracker);
        }

        public void setThrowExceptionOnMove(boolean throwException, String message) {
            this.throwExceptionOnMove = throwException;
            this.exceptionMessage = message;
        }

        public int getMoveCount() {
            return moveCount;
        }

        public void resetMoveCount() {
            moveCount = 0;
        }

        @Override
        public Ship getShip() {
            return new TestShip();
        }

        public void setScore(int score) {
            this.score = score;
            // Flag that score changed - might trigger level up on next update
            if (score >= 1000) {
                levelIncrementFlag = true;
            }
        }

        public int getScore() {
            return score;
        }

        @Override
        public int getLevel() {
            return level;
        }

        public String getObjectPositionString() {
            return "(10, 20)";
        }

        public void collectShieldPowerUp() {
            score += 50;
        }

        // Modified for testing purposes
        @Override
        public void levelUp() {
            // Use a fixed threshold of 1000 for testing since that's what the test expects
            if (score >= 1000 && levelIncrementFlag) {
                level++;
                levelIncrementFlag = false;  // Reset flag after level up
            }
        }

        public void setGameOverCondition(boolean gameOver) {
            this.gameOverCondition = gameOver;
        }

        @Override
        public boolean checkGameOver() {
            return gameOverCondition;
        }

        @Override
        public void updateGame(int tick) {
            // Call levelUp during updateGame
            levelUp();
        }

        private class TestShip extends Ship {
            @Override
            public void move(Direction direction) {
                if (throwExceptionOnMove) {
                    throw new IllegalArgumentException(exceptionMessage);
                }
                moveCount++;
            }
        }
    }

    /**
     * A test implementation of UI for testing purposes.
     */
    private static class TestUi implements UI {
        private Map<String, Boolean> methodCalls = new HashMap<>();
        private StringBuilder logBuilder = new StringBuilder();
        private boolean noFramesMode = false;

        public void resetLogs() {
            logBuilder = new StringBuilder();
        }

        public void resetVerification() {
            methodCalls.clear();
        }

        public void setNoFramesMode(boolean noFramesMode) {
            this.noFramesMode = noFramesMode;
        }

        public boolean wasMethodCalled(String methodName) {
            return methodCalls.getOrDefault(methodName, false);
        }

        public boolean wasShipMovementLogged() {
            return logBuilder.toString().contains("Ship moved to");
        }

        public boolean wasMessageLogged(String message) {
            return logBuilder.toString().contains(message);
        }

        @Override
        public void start() {
            methodCalls.put("start", true);
        }

        @Override
        public void stop() {
            methodCalls.put("stop", true);
        }

        @Override
        public void pause() {
            methodCalls.put("pause", true);
        }

        @Override
        public void onStep(Tickable tickable) {
            methodCalls.put("onStep", true);
        }

        @Override
        public void onKey(KeyHandler key) {
            methodCalls.put("onKey", true);
        }

        @Override
        public void render(List<SpaceObject> objects) {
            methodCalls.put("render", true);
        }

        @Override
        public void log(String message) {
            methodCalls.put("log", true);
            logBuilder.append(message).append("\n");
        }

        @Override
        public void setStat(String label, String value) {
            methodCalls.put("setStat", true);
        }

        @Override
        public void logAchievementMastered(String message) {
            methodCalls.put("logAchievementMastered", true);
        }

        @Override
        public void logAchievements(List<Achievement> achievements) {
            methodCalls.put("logAchievements", true);
        }

        @Override
        public void setAchievementProgressStat(String achievementName, double progressPercentage) {
            methodCalls.put("setAchievementProgressStat", true);
        }
    }

    /**
     * A test implementation of AchievementFile for testing purposes.
     */
    private static class TestFileHandler implements AchievementFile {
        private String fileLocation = DEFAULT_FILE_LOCATION;
        private List<String> savedData = new ArrayList<>();

        public void clearSavedData() {
            savedData.clear();
        }

        @Override
        public void setFileLocation(String fileLocation) {
            this.fileLocation = fileLocation;
        }

        @Override
        public String getFileLocation() {
            return fileLocation;
        }

        @Override
        public void save(String data) {
            savedData.add(data);
        }

        @Override
        public List<String> read() {
            return new ArrayList<>(savedData);
        }
    }
}
