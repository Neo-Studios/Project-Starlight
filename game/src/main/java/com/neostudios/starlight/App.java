package com.neostudios.starlight;

import com.neostudios.starlight.assets.AssetManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Main game panel for Project Starlight. Handles game loop, input, and delegates rendering and state management.
 */
public class App extends JPanel implements NeoLightGame {
    // --- Constants ---
    private static final String CONFIG_PATH = "/assets/game.properties";
    private static final int TIMER_DELAY_MS = 16;

    // --- Core Managers ---
    private final ConfigManager configManager;
    private final InputManager inputManager;
    private final GameStateManager gameStateManager;
    private final Renderer renderer;
    private final AssetManager assetManager = new AssetManager();

    // --- Game State ---
    private final Map<String, BufferedImage> textures = new HashMap<>();
    private int playerX;
    private int playerY;
    private final int playerSize;
    private final int moveSpeed;

    // --- Demo Game Objects ---
    private final Enemy demoEnemy;
    private final Bullet demoBullet;

    public App() {
        // Load configuration
        ConfigManager cfg;
        try {
            cfg = new ConfigManager(CONFIG_PATH);
        } catch (IOException e) {
            System.err.println("Warning: Could not load config file, using defaults. " + e.getMessage());
            cfg = null;
        }
        this.configManager = cfg;
        this.inputManager = new InputManager();
        this.gameStateManager = new GameStateManager();
        this.renderer = new Renderer();
        // Load textures
        loadTextures();
        // Window and player config
        int width = getIntConfig("window.width", 500);
        int height = getIntConfig("window.height", 500);
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        setBackground(Color.BLACK);
        this.playerX = getIntConfig("player.start_x", 200);
        this.playerY = getIntConfig("player.start_y", 200);
        this.playerSize = getIntConfig("player.size", 40);
        this.moveSpeed = getIntConfig("player.move_speed", 5);
        // Input
        addKeyListener(inputManager);
        // Timer/game loop
        Timer timer = new Timer(TIMER_DELAY_MS, e -> gameLoop());
        timer.start();
        // Demo objects
        demoEnemy = new Enemy(100, 100, textures.get("enemy.png"));
        demoBullet = new Bullet(playerX + 10, playerY - 20, textures.get("bullet.png"));
    }

    private int getIntConfig(String key, int def) {
        if (configManager != null) {
            return configManager.getIntProperty(key, def);
        }
        return def;
    }

    /**
     * Loads textures using getResourceAsStream for JAR compatibility.
     */
    private void loadTextures() {
        String[] textureFiles = {
            "assets/textures/player.png", "assets/textures/enemy.png", "assets/textures/background.png", "assets/textures/bullet.png", "assets/textures/powerup.png", "assets/textures/explosion.png",
            "assets/textures/ui_heart.png", "assets/textures/ui_coin.png", "assets/textures/wall.png", "assets/textures/floor.png"
        };
        for (String file : textureFiles) {
            java.awt.image.BufferedImage img = assetManager.getImage(file);
            if (img != null) {
                textures.put(file.substring(file.lastIndexOf('/') + 1), img);
            }
        }
    }

    /**
     * Main game loop: handles input, updates state, and repaints.
     */
    private void gameLoop() {
        handleInput();
        repaint();
    }

    /**
     * Handles input using InputManager and updates game state accordingly.
     */
    private void handleInput() {
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

    /**
     * Delegates rendering to the Renderer class.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(gameStateManager.getCurrentState(), g, this);
        // For now, keep legacy rendering for demonstration:
        switch (gameStateManager.getCurrentState()) {
            case MENU -> {
                g.setColor(Color.WHITE);
                g.drawString("Press ENTER to Start", 180, 250);
            }
            case PLAYING -> {
                BufferedImage bg = textures.get("background.png");
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
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

    @Override
    public void onInit() {
        // Initialize or reset game state here
    }

    @Override
    public void onUpdate() {
        handleInput();
    }

    @Override
    public void onRender(Graphics g) {
        paintComponent(g);
    }

    @Override
    public void onPause() {
        // Optional: handle pause logic
    }

    @Override
    public void onResume() {
        // Optional: handle resume logic
    }

    @Override
    public void onShutdown() {
        assetManager.clear();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    // --- For testing purposes ---
    int getPlayerX() { return playerX; }
    int getPlayerY() { return playerY; }
}
