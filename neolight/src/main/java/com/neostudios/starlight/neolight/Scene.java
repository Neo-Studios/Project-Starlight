package com.neostudios.starlight.neolight;

import java.awt.Graphics;

/**
 * Scene abstraction for use with SceneManager.
 * Extend this class to implement game-specific scenes.
 */
public abstract class Scene {
    /**
     * Called when the scene is initialized.
     */
    public void onInit() {}

    /**
     * Called every frame to update the scene.
     * @param deltaTime The time elapsed since the last frame, in seconds.
     */
    public void onUpdate(double deltaTime) {}

    /**
     * Called every frame to render the scene.
     * @param g The graphics context to use for rendering.
     */
    public void onRender(Graphics g) {}

    /**
     * Called when the scene is paused.
     */
    public void onPause() {}

    /**
     * Called when the scene is resumed.
     */
    public void onResume() {}

    /**
     * Called when the scene is shut down.
     */
    public void onShutdown() {}
}
