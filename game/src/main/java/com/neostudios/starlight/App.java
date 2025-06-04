package com.neostudios.starlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

    public App() {
        this.player = new Player("Player1");
        loadTextures();
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(500, 500));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (gameState == GameState.MENU && key == KeyEvent.VK_ENTER) {
                    setGameState(GameState.PLAYING);
                } else if (gameState == GameState.PLAYING) {
                    // Use enhanced switch (rule switch) for key handling
                    switch (key) {
                        case KeyEvent.VK_LEFT -> playerX -= MOVE_SPEED;
                        case KeyEvent.VK_RIGHT -> playerX += MOVE_SPEED;
                        case KeyEvent.VK_UP -> playerY -= MOVE_SPEED;
                        case KeyEvent.VK_DOWN -> playerY += MOVE_SPEED;
                        case KeyEvent.VK_P -> setGameState(GameState.PAUSED);
                    }
                } else if (gameState == GameState.PAUSED && key == KeyEvent.VK_P) {
                    setGameState(GameState.PLAYING);
                }
                repaint();
            }
        });
        Timer timer = new Timer(16, e -> repaint());
        timer.start();
        playerX = PLAYER_START_X;
        playerY = PLAYER_START_Y;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
        repaint();
    }

    public GameState getGameState() {
        return this.gameState;
    }

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
                // Example: Draw an enemy
                BufferedImage enemyImg = textures.get("enemy.png");
                if (enemyImg != null) {
                    g.drawImage(enemyImg, 100, 100, PLAYER_SIZE, PLAYER_SIZE, null);
                }
                // Example: Draw a bullet
                BufferedImage bulletImg = textures.get("bullet.png");
                if (bulletImg != null) {
                    g.drawImage(bulletImg, playerX + 10, playerY - 20, 16, 16, null);
                }
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
