package game.achievements;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Implementation of the AchievementFile interface for handling file operations.
 */
public class FileHandler implements AchievementFile {

    private String fileLocation;

    /**
     * Constructs a new FileHandler with the default file location.
     */
    public FileHandler() {
        this.fileLocation = DEFAULT_FILE_LOCATION;
    }

    /**
     * Constructs a new FileHandler with a specific file location.
     * @param fileLocation the file location to use
     */
    public FileHandler(String fileLocation) {
        if (fileLocation == null || fileLocation.isEmpty()) {
            throw new IllegalArgumentException("File location cannot be null or empty");
        }
        this.fileLocation = fileLocation;
    }

    @Override
    public void setFileLocation(String fileLocation) {
        if (fileLocation == null || fileLocation.isEmpty()) {
            throw new IllegalArgumentException("File location cannot be null or empty");
        }
        this.fileLocation = fileLocation;
    }

    @Override
    public String getFileLocation() {
        return fileLocation;
    }

    @Override
    public void save(String data) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileLocation),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save data to file: " + fileLocation, e);
        }
    }

    @Override
    public List<String> read() {
        List<String> dataList = new ArrayList<>();
        File file = new File(fileLocation);

        if (!file.exists()) {
            return dataList; // Return empty list if file doesn't exist
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileLocation))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read data from file: " + fileLocation, e);
        }

        return dataList;
    }
}