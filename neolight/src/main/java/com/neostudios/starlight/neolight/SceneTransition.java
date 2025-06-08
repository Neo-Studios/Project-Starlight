package com.neostudios.starlight.neolight;

/**
 * Interface for scene transition effects.
 * Implementations should handle the visual transition between scenes.
 */
public interface SceneTransition {
    /**
     * Starts the transition effect.
     * @param onComplete Callback to be executed when the transition is ready to switch scenes
     */
    void start(Runnable onComplete);
    
    /**
     * Updates the transition effect.
     * @param deltaTime Time elapsed since last update in seconds
     */
    void update(double deltaTime);
    
    /**
     * Renders the transition effect.
     * @param g The graphics context
     */
    void render(java.awt.Graphics2D g);
    
    /**
     * Ends the transition effect.
     * @param onComplete Callback to be executed when the transition is fully complete
     */
    void end(Runnable onComplete);
} 