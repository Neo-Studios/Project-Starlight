package com.neostudios.starlight;

import java.awt.Graphics;

/**
 * Scene interface for NeoLight engine. Each scene (menu, gameplay, etc.) implements this.
 */
public interface Scene {
    void onEnter();
    void onExit();
    void update();
    void render(Graphics g);
    default void onEvent(Object event) {}
}
