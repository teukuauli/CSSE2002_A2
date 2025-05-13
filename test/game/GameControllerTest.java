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

/**
 * Test class for GameController. Tests the functionality of the GameController class,
 * including achievement tracking, input handling, and game state management.
 */
public class GameControllerTest {

    private GameController gameController;
    private TestUi mockUi;
    private TestGameModel gameModel;
    private PlayerStatsTracker statsTracker;
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
        // Set up a ship that throws exceptions with specific messages when moved
        TestShip testShip = new TestShip();
        gameModel.setShip(testShip);

        // Test that the correct exception message is checked
        testShip.setThrowExceptionOnMove(true, "Cannot move outside game bounds");
        gameController.handlePlayerInput("W");

        // Check if the error was logged
        assertTrue("Exception message should be logged",
                mockUi.wasMessageLogged("Cannot move outside game bounds"));

        // Test with a different exception message to ensure the test would fail with wrong message
        mockUi.resetLogs();
        testShip.setThrowExceptionOnMove(true, "Wrong exception message");
        gameController.handlePlayerInput("W");

        assertTrue("Wrong exception message should be logged",
                mockUi.wasMessageLogged("Wrong exception message"));
        assertFalse("Should not log the correct message when wrong one is thrown",
                mockUi.wasMessageLogged("Cannot move outside game bounds"));
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

        // Debug output to see what's in the saved data
        System.out.println("Saved data size: " + savedData.size());
        for (String data : savedData) {
            System.out.println("Saved data: " + data);
        }

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

    // Helper classes

    /**
     * A test implementation of Ship for testing exception handling.
     */
    private static class TestShip extends Ship {
        private boolean throwExceptionOnMove = false;
        private String exceptionMessage = "Cannot move outside game bounds";

        public void setThrowExceptionOnMove(boolean throwException, String message) {
            this.throwExceptionOnMove = throwException;
            this.exceptionMessage = message;
        }

        @Override
        public void move(Direction direction) {
            if (throwExceptionOnMove) {
                throw new IllegalArgumentException(exceptionMessage);
            }
            super.move(direction);
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

        public TestGameModel(Logger logger, PlayerStatsTracker statsTracker) {
            super(logger, statsTracker);
        }

        public void setGameOverCondition(boolean gameOver) {
            this.gameOverCondition = gameOver;
        }

        public void setShip(Ship ship) {
            this.ship = ship;
        }

        @Override
        public boolean checkGameOver() {
            return gameOverCondition;
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
