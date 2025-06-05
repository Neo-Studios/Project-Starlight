package com.neostudios.starlight;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

/**
 * SceneManager handles switching and updating scenes in the NeoLight engine.
 */
public class SceneManager {
    private final Map<String, Scene> scenes = new HashMap<>();
    private Scene currentScene;

    public void addScene(String name, Scene scene) {
        scenes.put(name, scene);
    }

    public void switchTo(String name) {
        if (currentScene != null) currentScene.onExit();
        currentScene = scenes.get(name);
        if (currentScene != null) currentScene.onEnter();
    }

    public void update() {
        if (currentScene != null) currentScene.update();
    }

    public void render(Graphics g) {
        if (currentScene != null) currentScene.render(g);
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
