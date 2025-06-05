package com.neostudios.starlight.neolight;

import com.neostudios.starlight.GameState;
import com.neostudios.starlight.App;
import java.awt.Graphics;

/**
 * Encapsulates all rendering logic for the game.
 */
public class Renderer {
    public void render(GameState gameState, Graphics g, App app) {
        // Example implementation: render based on game state using asset manager
        switch (gameState) {
            case MENU -> {
                g.setColor(java.awt.Color.WHITE);
                g.drawString("Press ENTER to Start", 180, 250);
            }
            case PLAYING -> {
                java.awt.image.BufferedImage bg = app.getAssetManager().getImage("assets/textures/background.png");
                if (bg != null) {
                    g.drawImage(bg, 0, 0, app.getWidth(), app.getHeight(), null);
                } else {
                    g.setColor(java.awt.Color.BLACK);
                    g.fillRect(0, 0, app.getWidth(), app.getHeight());
                }
                java.awt.image.BufferedImage playerImg = app.getAssetManager().getImage("assets/textures/player.png");
                if (playerImg != null) {
                    g.drawImage(playerImg, app.getPlayerX(), app.getPlayerY(), 40, 40, null);
                } else {
                    g.setColor(java.awt.Color.CYAN);
                    g.fillOval(app.getPlayerX(), app.getPlayerY(), 40, 40);
                }
                // Add more rendering as needed (enemies, bullets, UI, etc.)
            }
            case PAUSED -> {
                g.setColor(java.awt.Color.YELLOW);
                g.drawString("Game Paused", 200, 250);
            }
            case GAME_OVER -> {
                g.setColor(java.awt.Color.RED);
                g.drawString("Game Over", 200, 250);
            }
        }
    }
}
