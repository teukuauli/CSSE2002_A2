package game.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Represents the way an object is represented.
 */
public class ObjectGraphic {
    private final String textRepresentation;
    private final Image graphicalRepresentation;

    /**
     * Creates a new ObjectGraphic with the given text and image.
     * @param textRepresentation the given text
     * @param pathToImage the given image
     */
    public ObjectGraphic(String textRepresentation, String pathToImage) {
        this.textRepresentation = textRepresentation;
        try {
            this.graphicalRepresentation = ImageIO.read(new File(pathToImage));
        } catch (IOException e) {
            // programming error, turn into runtime
            throw new RuntimeException("Image path not found: " + pathToImage);
        }
    }

    /**
     * Returns the string representation of the ObjectGraphic.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        return textRepresentation;
    }

    /**
     * Returns the graphical representation of the ObjectGraphic.
     * @return the graphical representation
     */
    public Image getGraphicalRepresentation() {
        return graphicalRepresentation;
    }
}
