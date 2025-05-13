package game;

import game.achievements.*;
import game.ui.KeyHandler;
import game.ui.Tickable;
import game.ui.UI;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import game.core.SpaceObject;
import game.utility.Logger;

public class GameControllerTest {

    private GameController gameController;
    private TestUI mockUI;
    private TestGameModel gameModel;
    private PlayerStatsTracker statsTracker;
    private AchievementManager achievementManager;
    private TestFileHandler fileHandler;

    @Before
    public void setUp() {
        // Create the necessary objects for testing
        mockUI = new TestUI();
        statsTracker = new TestStatsTracker();
        gameModel = new TestGameModel(mockUI::log, statsTracker);

        // Create AchievementManager with a test FileHandler
        fileHandler = new TestFileHandler();
        achievementManager = new AchievementManager(fileHandler);

        // Add some test achievements
        achievementManager.addAchievement(new GameAchievement("Survivor", "Survive for 120 seconds"));
        achievementManager.addAchievement(new GameAchievement("Enemy Exterminator", "Hit 20 shots"));
        achievementManager.addAchievement(new GameAchievement("Sharp Shooter", "Achieve 99% accuracy"));

        // Initialize GameController with test objects
        gameController = new GameController(mockUI, gameModel, achievementManager);
    }

    // Test for Limitation #1 - Achievement System
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
                assertEquals("Survivor description should match", "Survive for 120 seconds", achievement.getDescription());
                foundSurvivor = true;
            } else if ("Enemy Exterminator".equals(achievement.getName())) {
                assertEquals("Enemy Exterminator description should match", "Hit 20 shots", achievement.getDescription());
                foundExterminator = true;
            } else if ("Sharp Shooter".equals(achievement.getName())) {
                assertEquals("Sharp Shooter description should match", "Achieve 99% accuracy", achievement.getDescription());
                foundSharpShooter = true;
            }
        }

        assertTrue("Should have Survivor achievement", foundSurvivor);
        assertTrue("Should have Enemy Exterminator achievement", foundExterminator);
        assertTrue("Should have Sharp Shooter achievement", foundSharpShooter);
    }

    // Test for Limitation #2 - Constructor Options
    @Test
    public void testConstructorWithAchievementManager() {
        // Test that constructor accepts UI, GameModel, and AchievementManager
        GameController controller = new GameController(mockUI, gameModel, achievementManager);
        assertNotNull("Controller should be created with UI, GameModel, and AchievementManager", controller);

        // Test that constructor accepts UI and AchievementManager
        GameController controller2 = new GameController(mockUI, achievementManager);
        assertNotNull("Controller should be created with UI and AchievementManager", controller2);
    }

    // Test for Limitation #3 - Missing Methods
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
                    assertTrue("Enemy Exterminator progress should be updated", achievement.getProgress() > 0);
                }
            }
        } catch (NoSuchMethodError e) {
            fail("refreshAchievements method does not exist");
        }
    }

    @Test
    public void testGetStatsTrackerMethod() {
        // Test that getStatsTracker method exists and returns the correct tracker
        PlayerStatsTracker returnedTracker = gameController.getStatsTracker();
        assertNotNull("getStatsTracker should return a non-null tracker", returnedTracker);
        assertEquals("getStatsTracker should return the same tracker instance", statsTracker, returnedTracker);
    }

    @Test
    public void testSetVerboseMethod() {
        // Test that setVerbose method exists and affects verbose state
        gameController.setVerbose(true);
        assertTrue("setVerbose method exists", true);

        // Test verbose behavior by triggering a verbose log
        mockUI.resetLogs();
        gameController.handlePlayerInput("W");

        // Test that verbose logging was triggered
        assertTrue("Verbose logging should be enabled", mockUI.wasShipMovementLogged());

        // Test turning verbose off
        gameController.setVerbose(false);
        mockUI.resetLogs();

        // Test that verbose logging is now disabled
        gameController.handlePlayerInput("W");
        assertFalse("Verbose logging should be disabled", mockUI.wasShipMovementLogged());
    }

    // Test for Limitation #4 - Game State Handling
    @Test
    public void testVerboseModeForDetailedLogging() {

    }

    @Test
    public void testAchievementMasteryNotification() {
        // Set up a test achievement that's almost mastered
        GameAchievement testAchievement = new GameAchievement("Test Achievement", "Test Description");
        testAchievement.setProgress(0.999); // Master tier starts at 0.999
        achievementManager.addAchievement(testAchievement);

        // Clear file handler saved data
        fileHandler.clearSavedData();

        // Refresh achievements to trigger mastery check
        gameController.refreshAchievements(100);

        // Check if mastery was logged to the file
        List<String> savedData = fileHandler.read();
        boolean masteryLogged = false;
        for (String data : savedData) {
            if (data.contains("Test Achievement - Mastered")) {
                masteryLogged = true;
                break;
            }
        }
        assertTrue("Achievement mastery should be logged to the file", masteryLogged);
    }

    @Test
    public void testGameOverHandling() {
        gameModel.setGameOverCondition(true);

        // Reset UI verification data
        mockUI.resetVerification();

        gameController.onTick(1);

        assertTrue("Game should be paused on game over", mockUI.wasMethodCalled("pause"));
    }

    // Test for Limitation #5 - Input Handling
    @Test
    public void testInputIntegrationWithAchievements() {
        // Test firing a bullet increments shots fired in stats tracker
        int initialShotsFired = statsTracker.getShotsFired();
        gameController.handlePlayerInput("F");
        assertEquals("Shots fired should increment when F is pressed",
                initialShotsFired + 1, statsTracker.getShotsFired());
    }

    // Test for Limitation #6 - Player Statistics
    @Test
    public void testPlayerStatisticsTracking() {
        // Test shot accuracy
        statsTracker.recordShotFired();
        statsTracker.recordShotFired();
        statsTracker.recordShotHit();
        assertEquals("Accuracy should be calculated correctly", 0.5, statsTracker.getAccuracy(), 0.001);

        // Test shots fired/hit tracking
        assertEquals("Shots fired should be tracked", 2, statsTracker.getShotsFired());
        assertEquals("Shots hit should be tracked", 1, statsTracker.getShotsHit());
    }

    // Test for Limitation #7 - Progress Tracking
    @Test
    public void testTierBasedProgression() {
        // Test tier progression
        GameAchievement testAchievement = new GameAchievement("Test Tier", "Test tier progression");

        // Novice tier (< 0.5)
        testAchievement.setProgress(0.4);
        assertEquals("Progress < 0.5 should be Novice tier", "Novice", testAchievement.getCurrentTier());

        // Expert tier (0.5 <= x < 0.999)
        testAchievement.setProgress(0.7);
        assertEquals("Progress >= 0.5 and < 0.999 should be Expert tier", "Expert", testAchievement.getCurrentTier());

        // Master tier (>= 0.999)
        testAchievement.setProgress(0.999);
        assertEquals("Progress >= 0.999 should be Master tier", "Master", testAchievement.getCurrentTier());
    }

    // Test for Limitation #8 - Game Flow
    @Test
    public void testRenderingWithAchievements() {
        // Test that achievements are included in rendering
        mockUI.resetVerification();
        gameController.renderGame();
        assertTrue("Game rendering should involve setting UI stats", mockUI.wasMethodCalled("setStat"));
    }

    @Test
    public void testExecutionOrderInOnTick() {
        // Test that the method executes without errors
        gameController.onTick(1);

        assertTrue("onTick should execute without errors", true);
    }

    @Test
    public void testAchievementRefreshDuringGameplay() {
        mockUI.resetVerification();

        // Call onTick
        gameController.onTick(1);

        assertTrue("UI methods should be called during achievement refresh",
                mockUI.wasMethodCalled("setStat") || mockUI.wasMethodCalled("setAchievementProgressStat"));
    }

    // Helper classes

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
            if (shotsFired == 0) return 0.0;
            return (double) shotsHit / shotsFired;
        }
    }

    private static class TestGameModel extends GameModel {
        private boolean gameOverCondition = false;

        public TestGameModel(Logger logger, PlayerStatsTracker statsTracker) {
            super(logger, statsTracker);
        }

        public void setGameOverCondition(boolean gameOver) {
            this.gameOverCondition = gameOver;
        }

        @Override
        public boolean checkGameOver() {
            return gameOverCondition;
        }
    }

    private static class TestUI implements UI {
        private Map<String, Boolean> methodCalls = new HashMap<>();
        private StringBuilder logBuilder = new StringBuilder();

        public void resetLogs() {
            logBuilder = new StringBuilder();
        }

        public void resetVerification() {
            methodCalls.clear();
        }

        public boolean wasMethodCalled(String methodName) {
            return methodCalls.getOrDefault(methodName, false);
        }

        public boolean wasShipMovementLogged() {
            return logBuilder.toString().contains("Ship moved to");
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
            return savedData;
        }
    }
}