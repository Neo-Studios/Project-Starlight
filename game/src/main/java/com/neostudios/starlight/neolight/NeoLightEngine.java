package com.neostudios.starlight.neolight;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * NeoLightEngine: Modular, extensible Java game engine for 2D games.
 * Now supports:
 * - Delta time in game loop for frame-rate independent logic
 * - Global event bus for decoupled communication
 * - Scene management (SceneManager)
 * - TimerManager for scheduled tasks
 * - Customizable window icon/title
 * - Engine-wide logging
 * - Shutdown hooks
 */
public class NeoLightEngine {
    private final NeoLightGame game;
    private final JFrame frame;
    private final Timer timer;
    private int fps = 60;
    private boolean paused = false;
    private final List<Runnable> shutdownHooks = new ArrayList<>();
    private final EventBus eventBus = new EventBus();
    private final SceneManager sceneManager = new SceneManager();
    private final TimerManager timerManager = new TimerManager();
    private long lastFrameTime = System.nanoTime();

    public NeoLightEngine(NeoLightGame game, String title, int width, int height, Image icon) {
        this.game = game;
        this.frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.add(game.getPanel());
        if (icon != null) frame.setIconImage(icon);
        frame.setVisible(true);
        // Game loop timer with delta time
        this.timer = new Timer(1000 / fps, e -> {
            if (!paused) {
                long now = System.nanoTime();
                double deltaTime = (now - lastFrameTime) / 1_000_000_000.0;
                lastFrameTime = now;
                timerManager.update(deltaTime);
                sceneManager.update(deltaTime);
                game.onUpdate();
                game.getPanel().repaint();
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void setFps(int fps) {
        this.fps = fps;
        timer.setDelay(1000 / fps);
    }

    public void setWindowTitle(String title) {
        frame.setTitle(title);
    }

    public void setWindowIcon(Image icon) {
        frame.setIconImage(icon);
    }

    public void start() {
        EngineLogger.info("Engine starting...");
        game.onInit();
        timer.start();
    }

    public void pause() {
        if (!paused) {
            paused = true;
            EngineLogger.info("Game paused.");
            game.onPause();
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            EngineLogger.info("Game resumed.");
            game.onResume();
        }
    }

    public void shutdown() {
        EngineLogger.info("Engine shutting down...");
        timer.stop();
        game.onShutdown();
        for (Runnable hook : shutdownHooks) hook.run();
    }

    public void addShutdownHook(Runnable hook) {
        shutdownHooks.add(hook);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public JFrame getWindow() {
        return frame;
    }

    public static void main(String[] args) {
        // Example: Launch App as a NeoLightGame
        com.neostudios.starlight.App app = new com.neostudios.starlight.App();
        int width = app.getPanel().getWidth() > 0 ? app.getPanel().getWidth() : 500;
        int height = app.getPanel().getHeight() > 0 ? app.getPanel().getHeight() : 500;
        NeoLightEngine engine = new NeoLightEngine(app, "Project Starlight - NeoLight", width, height, null);
        engine.start();
    }
}
