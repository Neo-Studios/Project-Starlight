package com.neostudios.starlight.neolight;

import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * The NeoLightGame interface defines the contract for any game to be run by the NeoLight engine.
 * Now includes lifecycle hooks for future extensibility.
 */
public interface NeoLightGame {
    void onInit();
    void onUpdate();
    void onRender(Graphics g);
    void onPause();
    void onResume();
    void onShutdown();
    JPanel getPanel();
}

// This file is a duplicate. The real NeoLightGame is in the neolight package.
