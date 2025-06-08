package com.neostudios.starlight;

import java.awt.Color;
import java.awt.Graphics2D;
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
    private Enemy demoEnemy;
    private Bullet demoBullet;
    private boolean isPaused;

    public StarlightScene(GameStateManager gsm, InputManager inputManager, Map<String, BufferedImage> textures, int playerX, int playerY, int playerSize, int moveSpeed) {
        this.gameStateManager = gsm;
        this.inputManager = inputManager;
        this.textures = textures;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerSize = playerSize;
        this.moveSpeed = moveSpeed;
        this.isPaused = false;
    }

    @Override
    public void onCreate() {
        // Initialize game objects
        this.demoEnemy = new Enemy(100, 100, textures.get("enemy.png"));
        this.demoBullet = new Bullet(playerX + 10, playerY - 20, textures.get("bullet.png"));
    }

    @Override
    public void onEnter() {
        isPaused = false;
        // Start any background music or ambient sounds
    }

    @Override
    public void onPause() {
        isPaused = true;
        // Pause any background music or ambient sounds
    }

    @Override
    public void onResume() {
        isPaused = false;
        // Resume any background music or ambient sounds
    }

    @Override
    public void onExit() {
        // Clean up any resources
        isPaused = true;
    }

    @Override
    public void update(double deltaTime) {
        if (isPaused) {
            return;
        }

        GameState state = gameStateManager.getCurrentState();
        if (state == GameState.MENU && inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_ENTER)) {
            gameStateManager.setState(GameState.PLAYING);
        } else if (state == GameState.PLAYING) {
            // Update player position based on input
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_LEFT)) {
                playerX -= moveSpeed * deltaTime * 60; // Normalize to 60 FPS
            }
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_RIGHT)) {
                playerX += moveSpeed * deltaTime * 60;
            }
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_UP)) {
                playerY -= moveSpeed * deltaTime * 60;
            }
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_DOWN)) {
                playerY += moveSpeed * deltaTime * 60;
            }
            
            // Update game objects
            demoEnemy.update(deltaTime);
            demoBullet.update(deltaTime);
            
            // Check for pause
            if (inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_P)) {
                gameStateManager.setState(GameState.PAUSED);
            }
        } else if (state == GameState.PAUSED && inputManager.isKeyPressed(java.awt.event.KeyEvent.VK_P)) {
            gameStateManager.setState(GameState.PLAYING);
        }
    }

    @Override
    public void render(Graphics2D g) {
        // Clear the screen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        GameState state = gameStateManager.getCurrentState();
        if (state == GameState.MENU) {
            renderMenu(g);
        } else if (state == GameState.PLAYING || state == GameState.PAUSED) {
            renderGame(g);
            if (state == GameState.PAUSED) {
                renderPauseOverlay(g);
            }
        }
    }

    private void renderMenu(Graphics2D g) {
        // Render menu background
        if (textures.containsKey("background.png")) {
            g.drawImage(textures.get("background.png"), 0, 0, getWidth(), getHeight(), null);
        }

        // Render menu text
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        String title = "Project Starlight";
        String startText = "Press ENTER to Start";
        
        int titleWidth = g.getFontMetrics().stringWidth(title);
        int startWidth = g.getFontMetrics().stringWidth(startText);
        
        g.drawString(title, (getWidth() - titleWidth) / 2, getHeight() / 3);
        g.drawString(startText, (getWidth() - startWidth) / 2, getHeight() * 2 / 3);
    }

    private void renderGame(Graphics2D g) {
        // Render game background
        if (textures.containsKey("background.png")) {
            g.drawImage(textures.get("background.png"), 0, 0, getWidth(), getHeight(), null);
        }

        // Render game objects
        demoEnemy.render(g);
        demoBullet.render(g);

        // Render player
        if (textures.containsKey("player.png")) {
            g.drawImage(textures.get("player.png"), playerX, playerY, playerSize, playerSize, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(playerX, playerY, playerSize, playerSize);
        }

        // Render UI
        renderUI(g);
    }

    private void renderPauseOverlay(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Pause text
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 36));
        String pauseText = "PAUSED";
        int textWidth = g.getFontMetrics().stringWidth(pauseText);
        g.drawString(pauseText, (getWidth() - textWidth) / 2, getHeight() / 2);
    }

    private void renderUI(Graphics2D g) {
        // Render health
        if (textures.containsKey("ui_heart.png")) {
            BufferedImage heart = textures.get("ui_heart.png");
            for (int i = 0; i < 3; i++) {
                g.drawImage(heart, 10 + i * 30, 10, 20, 20, null);
            }
        }

        // Render score
        g.setColor(Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16));
        g.drawString("Score: 0", getWidth() - 100, 30);
    }

    private int getWidth() {
        return 800; // TODO: Get from engine
    }

    private int getHeight() {
        return 600; // TODO: Get from engine
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
        void render(Graphics2D g) {
            if (texture != null) {
                g.drawImage(texture, x, y, playerSize, playerSize, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, playerSize, playerSize);
            }
        }
        void update(double deltaTime) {
            // Implementation of update method
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
        void render(Graphics2D g) {
            if (texture != null) {
                g.drawImage(texture, x, y, 16, 16, null);
            } else {
                g.setColor(Color.YELLOW);
                g.fillOval(x, y, 16, 16);
            }
        }
        void update(double deltaTime) {
            // Implementation of update method
        }
    }
}
