package game.ui.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;

class Log extends JPanel {
    private List<String> messages = new ArrayList<>();
    private int pixelWidth = 0;
    private int pixelHeight = 0;

    public Log() {
        this.setBackground(Color.black);
    }

    public void setGraphicalDimensions(int pixelWidth, int pixelHeight) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
    }

    public void log(String message) {
        messages.add(message);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int windowHeight = (messages.size() + 1) * 20;
        int y = 0;
        if (windowHeight > pixelHeight) {
            y = (pixelHeight - windowHeight);
        }
        for (String message : messages) {
            g.setColor(Color.white);
            g.drawString(message, 10, y);
            y += 20;
        }
    }
}
