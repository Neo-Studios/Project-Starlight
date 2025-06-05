package com.neostudios.starlight;

import javax.swing.*;
import java.awt.*;

/**
 * NeoLightEngine is a minimal custom game engine for Project Starlight.
 * It manages the game loop, window, and delegates update/render to the game.
 */
public class NeoLightEngine {
    private final NeoLightGame game;
    private final JFrame frame;
    private final Timer timer;
    private static final int FPS = 60;

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
        this.timer = new Timer(1000 / FPS, e -> {
            game.update();
            game.getPanel().repaint();
        });
    }

    public void start() {
        game.init();
        timer.start();
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
