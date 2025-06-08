package com.neostudios.starlight.neolight;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Manages game scenes with support for transitions, scene stacking, and proper lifecycle management.
 */
public class SceneManager {
    private static final Logger LOGGER = Logger.getLogger(SceneManager.class.getName());
    
    private final Map<String, Scene> scenes;
    private final Stack<Scene> sceneStack;
    private Scene currentScene;
    private SceneTransition currentTransition;
    private boolean isTransitioning;
    
    public SceneManager() {
        this.scenes = new ConcurrentHashMap<>();
        this.sceneStack = new Stack<>();
        this.isTransitioning = false;
    }
    
    /**
     * Adds a scene to the manager.
     * @param name The unique identifier for the scene
     * @param scene The scene instance
     */
    public void addScene(String name, Scene scene) {
        if (scenes.containsKey(name)) {
            LOGGER.warning("Scene '" + name + "' already exists. Overwriting.");
        }
        scenes.put(name, scene);
        LOGGER.info("Added scene: " + name);
    }
    
    /**
     * Removes a scene from the manager.
     * @param name The name of the scene to remove
     */
    public void removeScene(String name) {
        Scene scene = scenes.remove(name);
        if (scene != null) {
            scene.onExit();
            LOGGER.info("Removed scene: " + name);
        }
    }
    
    /**
     * Switches to a scene with an optional transition.
     * @param name The name of the scene to switch to
     * @param transition Optional transition effect (can be null)
     */
    public void switchTo(String name, SceneTransition transition) {
        Scene newScene = scenes.get(name);
        if (newScene == null) {
            LOGGER.severe("Scene not found: " + name);
            return;
        }
        
        if (isTransitioning) {
            LOGGER.warning("Already transitioning, ignoring switch request to: " + name);
            return;
        }
        
        if (transition != null) {
            isTransitioning = true;
            currentTransition = transition;
            transition.start(() -> {
                if (currentScene != null) {
                    currentScene.onExit();
                }
                currentScene = newScene;
                currentScene.onEnter();
                transition.end(() -> {
                    isTransitioning = false;
                    currentTransition = null;
                });
            });
        } else {
            if (currentScene != null) {
                currentScene.onExit();
            }
            currentScene = newScene;
            currentScene.onEnter();
        }
        
        LOGGER.info("Switched to scene: " + name);
    }
    
    /**
     * Pushes a scene onto the stack with an optional transition.
     * @param name The name of the scene to push
     * @param transition Optional transition effect (can be null)
     */
    public void pushScene(String name, SceneTransition transition) {
        Scene newScene = scenes.get(name);
        if (newScene == null) {
            LOGGER.severe("Scene not found: " + name);
            return;
        }
        
        if (isTransitioning) {
            LOGGER.warning("Already transitioning, ignoring push request to: " + name);
            return;
        }
        
        if (currentScene != null) {
            sceneStack.push(currentScene);
        }
        
        if (transition != null) {
            isTransitioning = true;
            currentTransition = transition;
            transition.start(() -> {
                if (currentScene != null) {
                    currentScene.onPause();
                }
                currentScene = newScene;
                currentScene.onEnter();
                transition.end(() -> {
                    isTransitioning = false;
                    currentTransition = null;
                });
            });
        } else {
            if (currentScene != null) {
                currentScene.onPause();
            }
            currentScene = newScene;
            currentScene.onEnter();
        }
        
        LOGGER.info("Pushed scene: " + name);
    }
    
    /**
     * Pops the top scene from the stack with an optional transition.
     * @param transition Optional transition effect (can be null)
     * @return The popped scene, or null if the stack was empty
     */
    public Scene popScene(SceneTransition transition) {
        if (sceneStack.isEmpty()) {
            LOGGER.warning("Scene stack is empty, cannot pop");
            return null;
        }
        
        if (isTransitioning) {
            LOGGER.warning("Already transitioning, ignoring pop request");
            return null;
        }
        
        Scene previousScene = sceneStack.pop();
        
        if (transition != null) {
            isTransitioning = true;
            currentTransition = transition;
            transition.start(() -> {
                if (currentScene != null) {
                    currentScene.onExit();
                }
                currentScene = previousScene;
                currentScene.onResume();
                transition.end(() -> {
                    isTransitioning = false;
                    currentTransition = null;
                });
            });
        } else {
            if (currentScene != null) {
                currentScene.onExit();
            }
            currentScene = previousScene;
            currentScene.onResume();
        }
        
        LOGGER.info("Popped scene, current: " + (currentScene != null ? currentScene.getClass().getSimpleName() : "none"));
        return previousScene;
    }
    
    /**
     * Updates the current scene and any active transition.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (isTransitioning && currentTransition != null) {
            currentTransition.update(deltaTime);
        }
        
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
    }
    
    /**
     * Renders the current scene and any active transition.
     * @param g The graphics context
     */
    public void render(java.awt.Graphics2D g) {
        if (currentScene != null) {
            currentScene.render(g);
        }
        
        if (isTransitioning && currentTransition != null) {
            currentTransition.render(g);
        }
    }
    
    /**
     * Gets the current scene.
     * @return The current scene, or null if none is active
     */
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    /**
     * Checks if a scene exists.
     * @param name The name of the scene to check
     * @return true if the scene exists, false otherwise
     */
    public boolean hasScene(String name) {
        return scenes.containsKey(name);
    }
    
    /**
     * Gets the number of scenes in the stack.
     * @return The number of scenes in the stack
     */
    public int getStackSize() {
        return sceneStack.size();
    }
    
    /**
     * Clears all scenes and the stack.
     */
    public void clear() {
        if (currentScene != null) {
            currentScene.onExit();
            currentScene = null;
        }
        
        while (!sceneStack.isEmpty()) {
            Scene scene = sceneStack.pop();
            scene.onExit();
        }
        
        scenes.clear();
        LOGGER.info("Cleared all scenes");
    }
}
