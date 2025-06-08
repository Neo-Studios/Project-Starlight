package com.neostudios.starlight.neolight;

/**
 * Base class for games using the NeoLight engine.
 */
public interface NeoLightGame {
    /**
     * Called when the game is initialized.
     */
    void init();
    
    /**
     * Called every frame to render the game.
     * @param g The graphics context
     */
    void render(java.awt.Graphics2D g);
    // Add any other required methods for the engine
}
