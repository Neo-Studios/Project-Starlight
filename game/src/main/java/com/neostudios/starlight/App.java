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
    private int playerX = 200;
    private int playerY = 200;
    private final int playerSize = 40;
    private int moveSpeed = 5;

    public App() {
        setFocusable(true);
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(500, 500));
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) playerX -= moveSpeed;
                if (key == KeyEvent.VK_RIGHT) playerX += moveSpeed;
                if (key == KeyEvent.VK_UP) playerY -= moveSpeed;
                if (key == KeyEvent.VK_DOWN) playerY += moveSpeed;
                repaint();
            }
        });
        Timer timer = new Timer(16, e -> repaint());
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.CYAN);
        g.fillOval(playerX, playerY, playerSize, playerSize);
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
