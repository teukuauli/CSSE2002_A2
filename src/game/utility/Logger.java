package game.utility;

/**
 * Functional interface for passing logging information between classes.
 */
public interface Logger {
    /**
     * Logs provided text.
     *
     * @param text logging text
     */
    public void log(String text);
}
