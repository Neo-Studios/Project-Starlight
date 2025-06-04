package com.neostudios.starlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class App extends JPanel {
    @SuppressWarnings("unused")
    // The player object is currently not used, but will be used for future encapsulation of player state and logic
    private final Player player;
    private final Map<String, BufferedImage> textures = new HashMap<>();
    private static final int PLAYER_START_X = 200;
    private static final int PLAYER_START_Y = 200;
    private static final int PLAYER_SIZE = 40;
    private static final int MOVE_SPEED = 5;
    private int playerX;
    private int playerY;

    private GameState gameState = GameState.MENU;
    private static final String CONFIG_PATH = "game/assets/game.properties";
    private final Properties config = new Properties();

    private void loadTextures() {
        try {
            URL dirURL = getClass().getResource("/assets/textures/");
            if (dirURL != null) {
                File textureDir = new File(dirURL.toURI());
                File[] files = textureDir.listFiles((d, name) -> name.endsWith(".png"));
                if (files != null) {
                    for (File file : files) {
                        try {
                            BufferedImage img = ImageIO.read(file);
                            textures.put(file.getName(), img);
                        } catch (java.io.IOException | IllegalArgumentException e) {
                            System.err.println("Warning: Could not load texture: " + file.getName());
                        }
                    }
                }
            }
        } catch (java.net.URISyntaxException e) {
            System.err.println("Error loading textures from assets/textures: " + e.getMessage());
        }
    }

    private void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            config.load(fis);
        } catch (Exception e) {
            System.err.println("Warning: Could not load config file, using defaults. " + e.getMessage());
        }
    }

    // Flexible input management
    private final Map<Integer, Boolean> keyStates = new HashMap<>();

    private void setupInputManagement() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyStates.put(e.getKeyCode(), true);
                handleInput();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyStates.put(e.getKeyCode(), false);
            }
        });
    }

    private void handleInput() {
        if (gameState == GameState.MENU && keyStates.getOrDefault(KeyEvent.VK_ENTER, false)) {
            setGameState(GameState.PLAYING);
        } else if (gameState == GameState.PLAYING) {
            int moveSpeed = Integer.parseInt(config.getProperty("player.move_speed", String.valueOf(MOVE_SPEED)));
            // Process continuous movement based on key states
            if (keyStates.getOrDefault(KeyEvent.VK_LEFT, false)) playerX -= moveSpeed;
            if (keyStates.getOrDefault(KeyEvent.VK_RIGHT, false)) playerX += moveSpeed;
            if (keyStates.getOrDefault(KeyEvent.VK_UP, false)) playerY -= moveSpeed;
            if (keyStates.getOrDefault(KeyEvent.VK_DOWN, false)) playerY += moveSpeed;
            if (keyStates.getOrDefault(KeyEvent.VK_P, false)) setGameState(GameState.PAUSED);
        } else if (gameState == GameState.PAUSED && keyStates.getOrDefault(KeyEvent.VK_P, false)) {
            setGameState(GameState.PLAYING);
        }
        repaint();
    }

    // --- Game logic separation example ---
    // In a real project, these would be separate classes/files.
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
                g.drawImage(texture, x, y, App.PLAYER_SIZE, App.PLAYER_SIZE, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, App.PLAYER_SIZE, App.PLAYER_SIZE);
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

    // Example game objects for demonstration
    private final Enemy demoEnemy;
    private final Bullet demoBullet;

    public App() {
        loadConfig();
        this.player = new Player("Player1");
        loadTextures();
        setFocusable(true);
        setBackground(Color.BLACK);
        int width = Integer.parseInt(config.getProperty("window.width", "500"));
        int height = Integer.parseInt(config.getProperty("window.height", "500"));
        setPreferredSize(new Dimension(width, height));
        playerX = Integer.parseInt(config.getProperty("player.start_x", String.valueOf(PLAYER_START_X)));
        playerY = Integer.parseInt(config.getProperty("player.start_y", String.valueOf(PLAYER_START_Y)));
        setupInputManagement();
        Timer timer = new Timer(16, e -> repaint());
        timer.start();
        // Instantiate demo game objects
        demoEnemy = new Enemy(100, 100, textures.get("enemy.png"));
        demoBullet = new Bullet(playerX + 10, playerY - 20, textures.get("bullet.png"));
    }

    public void setGameState(GameState state) {
        this.gameState = state;
        repaint();
    }

    public GameState getGameState() {
        return this.gameState;
    }

    // For testing purposes
    int getPlayerX() { return playerX; }
    int getPlayerY() { return playerY; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        switch (gameState) {
            case MENU -> {
                g.setColor(Color.WHITE);
                g.drawString("Press ENTER to Start", 180, 250);
            }
            case PLAYING -> {
                // Draw background
                BufferedImage bg = textures.get("background.png");
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                // Draw player
                BufferedImage playerImg = textures.get("player.png");
                if (playerImg != null) {
                    g.drawImage(playerImg, playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, null);
                } else {
                    g.setColor(Color.CYAN);
                    g.fillOval(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
                }
                // Draw enemy using separated logic
                if (demoEnemy != null) demoEnemy.render(g);
                // Draw bullet using separated logic
                if (demoBullet != null) demoBullet.render(g);
                // Example: Draw a powerup
                BufferedImage powerupImg = textures.get("powerup.png");
                if (powerupImg != null) {
                    g.drawImage(powerupImg, 300, 300, 16, 16, null);
                }
                // Example: Draw an explosion
                BufferedImage explosionImg = textures.get("explosion.png");
                if (explosionImg != null) {
                    g.drawImage(explosionImg, 150, 150, 16, 16, null);
                }
                // Example: Draw UI heart
                BufferedImage heartImg = textures.get("ui_heart.png");
                if (heartImg != null) {
                    g.drawImage(heartImg, 10, 10, 16, 16, null);
                }
                // Example: Draw UI coin
                BufferedImage coinImg = textures.get("ui_coin.png");
                if (coinImg != null) {
                    g.drawImage(coinImg, 30, 10, 16, 16, null);
                }
                // Example: Draw wall
                BufferedImage wallImg = textures.get("wall.png");
                if (wallImg != null) {
                    g.drawImage(wallImg, 50, 400, 16, 16, null);
                }
                // Example: Draw floor
                BufferedImage floorImg = textures.get("floor.png");
                if (floorImg != null) {
                    g.drawImage(floorImg, 70, 400, 16, 16, null);
                }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Project Starlight - Simple Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new App());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
