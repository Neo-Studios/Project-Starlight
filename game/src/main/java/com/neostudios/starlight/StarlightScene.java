package com.neostudios.starlight;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.neostudios.starlight.neolight.InputManager;
import com.neostudios.starlight.neolight.Scene;

/**
 * Main scene for Project Starlight, handles all rendering and update logic for the game.
 */
public class StarlightScene extends Scene {
    private final GameStateManager gameStateManager;
    private final InputManager inputManager;
    private final Map<String, BufferedImage> textures;
    private final int playerSize;
    private final int moveSpeed;
    private int playerX, playerY;
    private final Enemy demoEnemy;
    private final Bullet demoBullet;

    public StarlightScene(GameStateManager gsm, InputManager inputManager, Map<String, BufferedImage> textures, int playerX, int playerY, int playerSize, int moveSpeed) {
        this.gameStateManager = gsm;
        this.inputManager = inputManager;
        this.textures = textures;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerSize = playerSize;
        this.moveSpeed = moveSpeed;
        this.demoEnemy = new Enemy(100, 100, textures.get("enemy.png"));
        this.demoBullet = new Bullet(playerX + 10, playerY - 20, textures.get("bullet.png"));
    }

    public void update(double deltaTime) {
        GameState state = gameStateManager.getCurrentState();
        if (state == GameState.MENU && inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_ENTER)) {
            gameStateManager.setState(GameState.PLAYING);
        } else if (state == GameState.PLAYING) {
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_LEFT)) playerX -= moveSpeed;
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_RIGHT)) playerX += moveSpeed;
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_UP)) playerY -= moveSpeed;
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN)) playerY += moveSpeed;
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_P)) gameStateManager.setState(GameState.PAUSED);
        } else if (state == GameState.PAUSED && inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_P)) {
            gameStateManager.setState(GameState.PLAYING);
        }
    }

    public void render(Graphics g) {
        int width = 500;
        int height = 500;
        switch (gameStateManager.getCurrentState()) {
            case MENU -> {
                g.setColor(Color.WHITE);
                g.drawString("Press ENTER to Start", 180, 250);
            }
            case PLAYING -> {
                BufferedImage bg = textures.get("background.png");
                if (bg != null) {
                    g.drawImage(bg, 0, 0, width, height, null);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, width, height);
                }
                BufferedImage playerImg = textures.get("player.png");
                if (playerImg != null) {
                    g.drawImage(playerImg, playerX, playerY, playerSize, playerSize, null);
                } else {
                    g.setColor(Color.CYAN);
                    g.fillOval(playerX, playerY, playerSize, playerSize);
                }
                if (demoEnemy != null) demoEnemy.render(g);
                if (demoBullet != null) demoBullet.render(g);
                BufferedImage powerupImg = textures.get("powerup.png");
                if (powerupImg != null) g.drawImage(powerupImg, 300, 300, 16, 16, null);
                BufferedImage explosionImg = textures.get("explosion.png");
                if (explosionImg != null) g.drawImage(explosionImg, 150, 150, 16, 16, null);
                BufferedImage heartImg = textures.get("ui_heart.png");
                if (heartImg != null) g.drawImage(heartImg, 10, 10, 16, 16, null);
                BufferedImage coinImg = textures.get("ui_coin.png");
                if (coinImg != null) g.drawImage(coinImg, 30, 10, 16, 16, null);
                BufferedImage wallImg = textures.get("wall.png");
                if (wallImg != null) g.drawImage(wallImg, 50, 400, 16, 16, null);
                BufferedImage floorImg = textures.get("floor.png");
                if (floorImg != null) g.drawImage(floorImg, 70, 400, 16, 16, null);
            }
            case PAUSED -> {
                g.setColor(Color.YELLOW);
                g.drawString("Game Paused", 200, 250);
            }
            case GAME_OVER -> {
                g.setColor(Color.RED);
                g.drawString("Game Over", 200, 250);
            }
        }
    }

    // --- Game object classes (should be moved to their own files for larger projects) ---
    class Enemy {
        int x, y;
        BufferedImage texture;
        Enemy(int x, int y, BufferedImage texture) {
            this.x = x;
            this.y = y;
            this.texture = texture;
        }
        void render(Graphics g) {
            if (texture != null) {
                g.drawImage(texture, x, y, playerSize, playerSize, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, playerSize, playerSize);
            }
        }
    }
    class Bullet {
        int x, y;
        BufferedImage texture;
        Bullet(int x, int y, BufferedImage texture) {
            this.x = x;
            this.y = y;
            this.texture = texture;
        }
        void render(Graphics g) {
            if (texture != null) {
                g.drawImage(texture, x, y, 16, 16, null);
            } else {
                g.setColor(Color.YELLOW);
                g.fillOval(x, y, 16, 16);
            }
        }
    }
}
