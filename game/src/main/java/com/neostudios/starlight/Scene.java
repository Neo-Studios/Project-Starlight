package com.neostudios.starlight.neolight;

import java.awt.Graphics;

/**
 * Scene abstraction for use with SceneManager.
 */
public abstract class Scene {
    public void onInit() {}
    public void onUpdate(double deltaTime) {}
    public void onRender(Graphics g) {}
    public void onPause() {}
    public void onResume() {}
    public void onShutdown() {}
}

// This file is a duplicate. The real Scene.java is in the neolight package.
