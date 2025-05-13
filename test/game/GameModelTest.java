package game.tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import game.GameModel;
import game.achievements.PlayerStatsTracker;
import game.utility.Logger;

/**
 * Simple tests for GameModel focusing on the key changes from A1 to A2.
 */
public class GameModelTest {

    private TestLogger logger;
    private PlayerStatsTracker statsTracker;
    private GameModel model;

    /**
     * Simple logger for testing
     */
    private static class TestLogger implements Logger {
        public String lastMessage = "";

        @Override
        public void log(String message) {
            lastMessage = message;
        }
    }

    @Before
    public void setUp() {
        logger = new TestLogger();
        statsTracker = new PlayerStatsTracker();
        model = new GameModel(logger, statsTracker);
    }

    @After
    public void tearDown() {
        logger = null;
        statsTracker = null;
        model = null;
    }

    /**
     * Test that the constructor now accepts a PlayerStatsTracker
     */
    @Test
    public void testConstructorWithStatsTracker() {
        assertNotNull("Model should be initialized", model);
    }

    /**
     * Test the getStatsTracker method
     */
    @Test
    public void testGetStatsTracker() {
        PlayerStatsTracker result = model.getStatsTracker();
        assertNotNull("StatsTracker should not be null", result);
        assertSame("Should return the same statsTracker", statsTracker, result);
    }

    /**
     * Test that the setVerbose method exists
     */
    @Test
    public void testSetVerbose() {
        // Just verify the method exists and runs without errors
        model.setVerbose(true);
        model.setVerbose(false);
    }

    /**
     * Test that the checkGameOver method exists
     */
    @Test
    public void testCheckGameOver() {
        // Just verify the method exists and runs without errors
        boolean result = model.checkGameOver();
        assertFalse("Game should not be over initially", result);
    }
}
