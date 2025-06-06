package com.neostudios.starlight.neolight;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneManager handles switching between different scenes (e.g., menu, game, pause).
 */
public class SceneManager {
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene currentScene;

    public void addScene(String name, Scene scene) {
        scenes.put(name, scene);
    }

    public void switchTo(String name) {
        if (currentScene != null) currentScene.onPause();
        currentScene = scenes.get(name);
        if (currentScene != null) currentScene.onResume();
    }

    public void update(double deltaTime) {
        if (currentScene != null) currentScene.onUpdate(deltaTime);
    }

    /**
     * @deprecated Use update(double deltaTime) instead.
     */
    @Deprecated
    public void update() {
        update(0);
    }

    public void render(Graphics g) {
        if (currentScene != null) currentScene.onRender(g);
    }

    /**
     * @deprecated Use render(Graphics g) instead.
     */
    @Deprecated
    public void render() {
        render(null);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
