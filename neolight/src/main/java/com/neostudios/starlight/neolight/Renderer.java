package com.neostudios.starlight.neolight;

import java.awt.Graphics;

/**
 * Encapsulates all rendering logic for the engine or game.
 * This class is now generic and should be extended or used by the game module.
 */
public class Renderer {
    /**
     * Render method to be overridden or used by the game.
     * @param g the Graphics context
     */
    public void render(Graphics g) {
        // Default: do nothing. Game should override or provide its own logic.
    }
}
