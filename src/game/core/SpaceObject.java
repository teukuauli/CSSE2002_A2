package game.core;

import game.ui.ObjectGraphic;
import game.ui.Tickable;

/**
 * Represents an object in the space game.
 */
public interface SpaceObject extends Tickable {

    /**
     * Returns a new ObjectGraphic with the appropriate text representation and image path.
     *
     * @return the appropriate new ObjectGraphic.
     */
    ObjectGraphic render();

    // Methods for positional awareness
    /**
     * Returns the x coordinate of the SpaceObject,
     * where 0 represents the left-most space with positive numbers extending to the right.<br>
     *
     * @return x coordinate of the SpaceObject.
     */
    int getX();

    /**
     * Returns the y coordinate of the SpaceObject,
     * where 0 represents the top-most space with positive numbers extending downwards.<br>
     *
     * @return y coordinate of the SpaceObject.
     */
    int getY();
}
