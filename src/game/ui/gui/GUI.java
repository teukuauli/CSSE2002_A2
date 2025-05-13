package game.ui.gui;

import game.achievements.Achievement;
import game.core.SpaceObject;
import game.GameModel;
import game.ui.KeyHandler;
import game.ui.Tickable;
import game.ui.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a GUI implementation of the UI interface.
 */
public class GUI implements UI {
    private static final int TICK_SPEED = 100;
    private static final int WINDOW_HEIGHT = 600;
    private static final int WINDOW_WIDTH = 650;

    // GAME_HEIGHT + 1 required for bottom row (row 19) to render correctly
    private final Canvas canvas = new Canvas(GameModel.GAME_WIDTH, GameModel.GAME_HEIGHT + 1);
    private final Log log = new Log();
    private final Stats stats = new Stats();

    private final List<Tickable> tickables = new ArrayList<>();
    private final List<KeyHandler> handlers = new ArrayList<>();

    private Timer gameTimer;
    private int tick = 0;

    /**
     * Starts the GUI.
     */
    @Override
    public void start() {
        JFrame frame = new JFrame();
        frame.setTitle("Space Shooter");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT); // Sets default window size


        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addComponent(canvas)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(log)
                                        .addComponent(stats))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addComponent(canvas)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(log)
                                .addComponent(stats))
        );
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                int width = (int) Math.floor(size.getWidth());
                int height = (int) Math.floor(size.getHeight());
                canvas.setGraphicalDimensions((int) (width * 0.5), height);
                log.setGraphicalDimensions((int) (width * 0.5), (int) (height * 0.5));
            }
        });
        frame.setVisible(true);

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                for (KeyHandler handler : handlers) {
                    handler.onPress(e.getKeyChar() + "");
                }
                canvas.updateUI();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        gameTimer = new Timer(TICK_SPEED, (e) -> {
            for (Tickable tickable : tickables) {
                tickable.tick(tick++);
                canvas.updateUI();
            }
        });
        gameTimer.start();

        logWelcome();
    }

    /**
     * Displays the game welcome message and instructions.
     */
    private void logWelcome(){
        log("=====================================");
        log("       Welcome to Space Shooter!     ");
        log("=====================================");
        log("Controls: ");
        log("  W - Move Up");
        log("  A - Move Left");
        log("  S - Move Down");
        log("  D - Move Right");
        log("  F - Fire Bullets");
        log("  P - Pause Game");
    }

    /**
     * Alternates between paused and unpaused state.
     */
    @Override
    public void pause() {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        } else {
            gameTimer.start();
        }
    }

    /**
     * Pauses the game
     */
    @Override
    public void stop() {
        gameTimer.start();
    }

    /**
     * Stores the provided tickable.
     * @param tickable the provided tickable
     */
    @Override
    public void onStep(Tickable tickable) {
        tickables.add(tickable);
    }

    /**
     * Stores the provided KeyHandler.
     * @param key the provided KeyHandler
     */
    @Override
    public void onKey(KeyHandler key) {
        handlers.add(key);
    }

    /**
     * Renders the provided objects, and refreshes the UI.
     *
     * @param objects the list of SpaceObjects to be rendered.
     */
    @Override
    public void render(List<SpaceObject> objects) {
        canvas.updateSpaceObjects(objects);
        canvas.updateUI();
    }

    /**
     * Log the provided message.
     *
     * @param message the provided message
     */
    @Override
    public void log(String message) {
        log.log(message);
        log.updateUI();
    }

    /**
     * Sets the stats to the provided label and value.
     *
     * @param label the provided label
     * @param value the provided value
     */
    @Override
    public void setStat(String label, String value) {
        if (!stats.isRegistered(label)) {
            stats.registerStat(label);
        }
        stats.setStat(label, value);
        stats.updateUI();
    }

    @Override
    public void logAchievementMastered(String message) {
        log("Achievement Mastered: " + message);
    }

    @Override
    public void logAchievements(List<Achievement> achievements) {
        log("=== Achievements ===");
        for (Achievement ach : achievements) {
            StringBuilder sb = new StringBuilder();
            double progressPercent = ach.getProgress() * 100;
            sb.append(ach.getName())
                    .append(" - ").append(" (").append(String.format("%.0f%%", progressPercent))
                    .append(" complete, Tier: ").append(ach.getCurrentTier()).append(")");
            log(sb.toString());
        }
    }

    @Override
    public void setAchievementProgressStat(String achievementName, double progressPercentage) {
        double progress = 5*(Math.floor(Math.abs(progressPercentage * 100/5)));
        progress = progressPercentage > 0.99 ? 100 : progress;
        setStat(achievementName + " Progress", String.format("%.0f%%", progress));
    }
}

