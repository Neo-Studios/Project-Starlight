package com.neostudios.starlight;

import javax.swing.*;
import java.awt.*;

/**
 * The NeoLightGame interface defines the contract for any game to be run by the NeoLight engine.
 */
public interface NeoLightGame {
    void init();
    void update();
    void render(Graphics g);
    JPanel getPanel();
}
