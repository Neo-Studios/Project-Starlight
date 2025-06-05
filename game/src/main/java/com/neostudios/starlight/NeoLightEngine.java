package com.neostudios.starlight;

import javax.swing.*;
import java.util.List;

/**
 * NeoLightEngine is a minimal custom game engine for Project Starlight.
 * Now supports lifecycle events, logging, and a basic event system for extensibility.
 */
public class NeoLightEngine {
    private final NeoLightGame game;
    private final JFrame frame;
    private final Timer timer;
    private int fps = 60;
    private boolean paused = false;
    private final List<Runnable> shutdownHooks = new java.util.ArrayList<>();

    public NeoLightEngine(NeoLightGame game, String title, int width, int height) {
        this.game = game;
        this.frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.add(game.getPanel());
        frame.setVisible(true);
        // Game loop timer
        this.timer = new Timer(1000 / fps, e -> {
            if (!paused) {
                game.onUpdate();
                game.getPanel().repaint();
            }
        });
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void setFps(int fps) {
        this.fps = fps;
        timer.setDelay(1000 / fps);
    }

    public void start() {
        log("Engine starting...");
        game.onInit();
        timer.start();
    }

    public void pause() {
        if (!paused) {
            paused = true;
            log("Game paused.");
            game.onPause();
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            log("Game resumed.");
            game.onResume();
        }
    }

    public void shutdown() {
        log("Engine shutting down...");
        timer.stop();
        game.onShutdown();
        for (Runnable hook : shutdownHooks) hook.run();
    }

    public void addShutdownHook(Runnable hook) {
        shutdownHooks.add(hook);
    }

    public void log(String msg) {
        System.out.println("[NeoLight] " + msg);
    }

    public static void main(String[] args) {
        // Example: Launch App as a NeoLightGame
        App app = new App();
        int width = app.getPanel().getWidth() > 0 ? app.getPanel().getWidth() : 500;
        int height = app.getPanel().getHeight() > 0 ? app.getPanel().getHeight() : 500;
        NeoLightEngine engine = new NeoLightEngine(app, "Project Starlight - NeoLight", width, height);
        engine.start();
    }
}
