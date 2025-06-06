package com.neostudios.starlight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import com.neostudios.starlight.neolight.AssetManager;
import com.neostudios.starlight.neolight.InputManager;
import com.neostudios.starlight.neolight.NeoLightEngine;
import com.neostudios.starlight.neolight.SceneManager;

public class App extends JPanel implements com.neostudios.starlight.neolight.NeoLightGame {
    private static final String CONFIG_PATH = "/assets/game.properties";
    private final ConfigManager configManager;
    private final InputManager inputManager;
    private final GameStateManager gameStateManager;
    private final AssetManager assetManager = new AssetManager();
    private final Map<String, BufferedImage> textures = new HashMap<>();
    private final int playerX, playerY, playerSize, moveSpeed;
    private final StarlightScene starlightScene;
    private NeoLightEngine engine;

    public App() {
        super();
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
        // Create the main scene and register it
        this.starlightScene = new StarlightScene(gameStateManager, inputManager, textures, playerX, playerY, playerSize, moveSpeed);
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
            BufferedImage img = assetManager.getImage(file);
            if (img != null) {
                textures.put(file.substring(file.lastIndexOf('/') + 1), img);
            }
        }
    }

    /**
     * Delegates rendering to the current scene.
     */
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        // Delegate rendering to the current scene
        if (engine != null) {
            SceneManager sm = engine.getSceneManager();
            com.neostudios.starlight.neolight.Scene current = sm.getCurrentScene();
            if (current != null) current.onRender(g);
        }
    }

    @Override
    public void init() {
        // Initialization logic
        if (engine != null) {
            SceneManager sm = engine.getSceneManager();
            sm.addScene("main", starlightScene);
            sm.switchTo("main");
        }
    }

    @Override
    public void render(java.awt.Graphics g) {
        // Rendering logic (delegated to scene system)
    }

    public void setEngine(NeoLightEngine engine) {
        this.engine = engine;
    }

    // --- For testing purposes ---
    public int getPlayerX() { return playerX; }
    public int getPlayerY() { return playerY; }

    public static void main(String[] args) {
        App app = new App();
        int width = app.getPreferredSize().width > 0 ? app.getPreferredSize().width : 500;
        int height = app.getPreferredSize().height > 0 ? app.getPreferredSize().height : 500;
        NeoLightEngine engine = new NeoLightEngine(app, "Project Starlight - NeoLight", width, height, null);
        app.setEngine(engine);
        engine.start();
    }
}
