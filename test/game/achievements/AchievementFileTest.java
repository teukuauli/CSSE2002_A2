package game.achievements;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import java.io.File;
import java.util.List;

public class AchievementFileTest {

    private FileHandler fileHandler;

    @Before
    public void setUp() {
        fileHandler = new FileHandler();
    }

    @After
    public void tearDown() {
        // Clean up test files
        File defaultFile = new File(AchievementFile.DEFAULT_FILE_LOCATION);
        if (defaultFile.exists()) {
            defaultFile.delete();
        }

        File customFile = new File("test_achievements.dat");
        if (customFile.exists()) {
            customFile.delete();
        }
    }

    /**
     * Test that the default location matches the interface constant
     */
    @Test
    public void achievementFileTestLocation() {
        // Check that the default location matches the interface constant
        assertEquals("The default location for an AchievementFile should be '" +
                        AchievementFile.DEFAULT_FILE_LOCATION + "'",
                AchievementFile.DEFAULT_FILE_LOCATION, fileHandler.getFileLocation());
    }

    /**
     * Test that we can set a custom location
     */
    @Test
    public void achievementFileTestCustomLocation() {
        // Set a custom location
        String customLocation = "test_achievements.dat";
        fileHandler.setFileLocation(customLocation);

        // Check that the location is correctly set
        assertEquals("AchievementFile should use the custom location",
                customLocation, fileHandler.getFileLocation());
    }

    /**
     * Test saving and loading data
     */
    @Test
    public void achievementFileTestSaveLoad() {
        // Create some test data
        String testData1 = "Test data entry 1";
        String testData2 = "Test data entry 2";

        // Save the data
        fileHandler.save(testData1);
        fileHandler.save(testData2);

        // Load the data
        List<String> loadedData = fileHandler.read();

        // Check that we got something back
        assertNotNull("Loading data should not return null", loadedData);

        // Check that we got the right number of entries
        assertEquals("Should load the same number of entries that were saved",
                2, loadedData.size());

        // Check the contents
        assertTrue("Loaded data should contain first entry",
                loadedData.contains(testData1));
        assertTrue("Loaded data should contain second entry",
                loadedData.contains(testData2));
    }
}