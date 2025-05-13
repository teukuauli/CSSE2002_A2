package game.achievements;

import java.util.List;

/**
 * Handles file input/output operations for persisting achievement events data.
 */
public interface AchievementFile {

    /**
     * If setFileLocation is not called, this value should be used.
     */
    static final String DEFAULT_FILE_LOCATION = "achievements.dat";

    /**
     * Sets the file location to save to.
     * @param fileLocation the new file location
     */
    void setFileLocation(String fileLocation);

    /**
     * Gets the location currently being saved to.
     * @return the current file location
     */
    String getFileLocation();

    /**
     * Saves the given data to a file followed by a new-line character.
     * @param data the data to be saved.
     */
    void save(String data);

    /**
     * Loads and returns all previously saved data as a list of strings.
     * @return a list of saved data entries.
     */
    List<String> read();
}
