package com.neostudios.starlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class App extends JPanel {
    @SuppressWarnings("unused")
    // The player object is currently not used, but will be used for future encapsulation of player state and logic
    private final Player player;
    private static final int PLAYER_START_X = 200;
    private static final int PLAYER_START_Y = 200;
    private static final int PLAYER_SIZE = 40;
    private static final int MOVE_SPEED = 5;
    private int playerX;
    private int playerY;

    private GameState gameState = GameState.MENU;

    public App() {
        this.player = new Player("Player1");
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
        // Use enhanced switch (rule switch) for game state rendering
        switch (gameState) {
            case MENU -> {
                g.setColor(Color.WHITE);
                g.drawString("Press ENTER to Start", 180, 250);
            }
            case PLAYING -> {
                g.setColor(Color.CYAN);
                g.fillOval(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE);
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
