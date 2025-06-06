package com.neostudios.starlight.neolight;

/**
 * Base class for games using the NeoLight engine.
 */
public interface NeoLightGame {
    void init();
    void render(java.awt.Graphics g);
    // Add any other required methods for the engine
}
