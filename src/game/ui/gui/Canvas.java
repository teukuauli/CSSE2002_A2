package game.ui.gui;

import game.core.SpaceObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

class Canvas extends JPanel {
    private List<SpaceObject> objects = new ArrayList<>();
    private final int width;
    private final int height;
    private int pixelWidth = 0;
    private int pixelHeight = 0;

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.setBackground(Color.black);
    }

    public void setGraphicalDimensions(int pixelWidth, int pixelHeight) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public void updateSpaceObjects(List<SpaceObject> objects) {
        this.objects = objects;
    }

    private int getPixelX(int x) {
        return (x * (pixelWidth / width));
    }

    private int getPixelMidX(int x) {
        return getPixelX(x) + (pixelWidth / width / 2);
    }

    private int getPixelY(int y) {
        return (y * (pixelHeight / height));
    }

    private int getPixelMidY(int y) {
        return getPixelY(y) + (pixelHeight / height / 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (SpaceObject object : objects) {
            ImageObserver observer = (img, i, x, y, w, h) -> false;
            Image image = object.render().getGraphicalRepresentation();
            int x = getPixelMidX(object.getX()) - image.getWidth(observer) / 2;
            int y = getPixelMidY(object.getY()) - image.getHeight(observer) / 2;
            g.drawImage(image, x, y, observer);
        }
    }

    private void drawGrid(Graphics g) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g.setColor(Color.white);
                g.drawRect(getPixelX(x), getPixelY(y), getPixelX(x + 1), getPixelY(y + 1));
            }
        }
    }
}
