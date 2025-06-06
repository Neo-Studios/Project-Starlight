package com.neostudios.starlight.neolight;

/**
 * Core engine class for NeoLight. Handles main loop and scene management.
 */
public class NeoLightEngine {
    private SceneManager sceneManager;
    private boolean running = false;
    private long lastFrameTime;

    private final NeoLightGame game;
    private final String title;
    private final int width;
    private final int height;
    private final java.awt.Image icon;

    public NeoLightEngine(NeoLightGame game, String title, int width, int height, java.awt.Image icon) {
        this.game = game;
        this.title = title;
        this.width = width;
        this.height = height;
        this.icon = icon;
        this.sceneManager = new SceneManager();
    }

    public void start() {
        running = true;
        while (running) {
            double deltaTime = computeDeltaTime();
            sceneManager.update(deltaTime);
            // Rendering should be handled by the game or a provided callback
            // Optionally, expose a render(Graphics g) method for integration
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop() {
        running = false;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    // Utility for delta time calculation
    private double computeDeltaTime() {
        long now = System.nanoTime();
        double delta = (now - lastFrameTime) / 1_000_000_000.0;
        lastFrameTime = now;
        return delta;
    }
}
