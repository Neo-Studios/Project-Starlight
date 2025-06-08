package com.neostudios.starlight.neolight;

import java.awt.Graphics;

/**
 * Base class for all game scenes.
 * Provides lifecycle methods and basic functionality for scene management.
 */
public abstract class Scene {
    /**
     * Called when the scene is first created.
     * Use this for initialization that doesn't depend on other scenes.
     */
    public void onCreate() {
        // Default implementation does nothing
    }
    
    /**
     * Called when the scene becomes the active scene.
     * Use this for initialization that depends on other scenes or resources.
     */
    public void onEnter() {
        // Default implementation does nothing
    }
    
    /**
     * Called when the scene is paused (e.g., when another scene is pushed on top).
     * Use this to pause animations, audio, or other ongoing processes.
     */
    public void onPause() {
        // Default implementation does nothing
    }
    
    /**
     * Called when the scene is resumed (e.g., when a scene above it is popped).
     * Use this to resume animations, audio, or other processes.
     */
    public void onResume() {
        // Default implementation does nothing
    }
    
    /**
     * Called when the scene is no longer the active scene.
     * Use this to clean up resources that are no longer needed.
     */
    public void onExit() {
        // Default implementation does nothing
    }
    
    /**
     * Called every frame to update the scene's state.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        // Default implementation does nothing
    }
    
    /**
     * Called every frame to render the scene.
     * @param g The graphics context
     */
    public void render(java.awt.Graphics2D g) {
        // Default implementation does nothing
    }
    
    /**
     * Called when the scene is being destroyed.
     * Use this for final cleanup of resources.
     */
    public void onDestroy() {
        // Default implementation does nothing
    }
}
