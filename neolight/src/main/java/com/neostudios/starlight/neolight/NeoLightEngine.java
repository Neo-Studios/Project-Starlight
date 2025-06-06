package com.neostudios.starlight.neolight;

/**
 * Core engine class for NeoLight. Handles main loop and scene management.
 */
public class NeoLightEngine {
    private SceneManager sceneManager;
    private boolean running = false;
    private long lastFrameTime;

    private final int width;
    private final int height;
    private final java.awt.image.BufferedImage renderBuffer;
    private final java.awt.Graphics2D renderGraphics;
    private final javax.swing.JFrame frame;
    private final javax.swing.JPanel renderPanel;
    private final InputManager inputManager;
    private final MouseInputManager mouseInputManager;
    private volatile boolean isFocused = true;
    private final NeoLightGame game;

    /**
     * Creates a new NeoLightEngine instance with a game instance.
     * @param game The game instance to run
     * @param title The window title
     * @param width The window width
     * @param height The window height
     * @param icon The window icon (can be null)
     */
    public NeoLightEngine(NeoLightGame game, String title, int width, int height, java.awt.Image icon) {
        this.game = game;
        this.width = width;
        this.height = height;
        this.sceneManager = new SceneManager();
        this.lastFrameTime = System.nanoTime();
        this.inputManager = new InputManager();
        this.mouseInputManager = new MouseInputManager();
        
        // Initialize the render buffer with proper dimensions
        this.renderBuffer = new java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        this.renderGraphics = this.renderBuffer.createGraphics();
        // Enable anti-aliasing for smoother rendering
        this.renderGraphics.setRenderingHint(
            java.awt.RenderingHints.KEY_ANTIALIASING,
            java.awt.RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Create and setup the frame
        this.frame = new javax.swing.JFrame(title);
        if (icon != null) {
            this.frame.setIconImage(icon);
        }
        this.frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        this.frame.setResizable(false);

        // Create the render panel
        this.renderPanel = new javax.swing.JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                synchronized (renderBuffer) {
                    g.drawImage(renderBuffer, 0, 0, null);
                }
            }
        };
        this.renderPanel.setPreferredSize(new java.awt.Dimension(width, height));
        
        // Add input listeners
        this.frame.addKeyListener(inputManager);
        this.renderPanel.addMouseListener(mouseInputManager);
        this.renderPanel.addMouseMotionListener(mouseInputManager);
        
        // Add window focus listener
        this.frame.addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent e) {
                isFocused = true;
            }
            @Override
            public void windowLostFocus(java.awt.event.WindowEvent e) {
                isFocused = false;
            }
        });

        this.frame.add(this.renderPanel);
        this.frame.pack();
        this.frame.setLocationRelativeTo(null); // Center on screen
    }

    public void start() {
        // Initialize the game
        if (game != null) {
            game.init();
        }

        // Show the frame before starting the game loop
        javax.swing.SwingUtilities.invokeLater(() -> frame.setVisible(true));
        
        running = true;
        while (running) {
            if (!isFocused) {
                try {
                    Thread.sleep(100); // Sleep longer when window is not focused
                    continue;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            double deltaTime = computeDeltaTime();
            sceneManager.update(deltaTime);
            
            // Clear the buffer
            synchronized (renderBuffer) {
                renderGraphics.setColor(java.awt.Color.BLACK);
                renderGraphics.fillRect(0, 0, width, height);
                
                // Render the current scene
                sceneManager.render(renderGraphics);
                
                // Also render the game if it's not using the scene system
                if (game != null) {
                    game.render(renderGraphics);
                }
            }
            
            // Trigger a repaint of the panel on the EDT
            javax.swing.SwingUtilities.invokeLater(() -> renderPanel.repaint());
            
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
        renderGraphics.dispose(); // Clean up graphics resources
        javax.swing.SwingUtilities.invokeLater(() -> frame.dispose()); // Clean up the frame on EDT
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /**
     * Gets the InputManager instance used for handling keyboard input.
     */
    public InputManager getInputManager() {
        return inputManager;
    }

    /**
     * Gets the MouseInputManager instance used for handling mouse input.
     */
    public MouseInputManager getMouseInputManager() {
        return mouseInputManager;
    }

    /**
     * Gets the JFrame instance used for display.
     * This can be used to add input listeners or customize the window.
     */
    public javax.swing.JFrame getFrame() {
        return frame;
    }

    /**
     * Checks if the window currently has focus.
     */
    public boolean isFocused() {
        return isFocused;
    }

    /**
     * Gets the game instance associated with this engine.
     */
    public NeoLightGame getGame() {
        return game;
    }

    // Utility for delta time calculation
    private double computeDeltaTime() {
        long now = System.nanoTime();
        double delta = (now - lastFrameTime) / 1_000_000_000.0;
        lastFrameTime = now;
        return delta;
    }
}
