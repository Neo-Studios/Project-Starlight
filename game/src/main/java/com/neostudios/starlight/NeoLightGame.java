package com.neostudios.starlight;

import javax.swing.*;
import java.awt.*;

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
