package com.neostudios.starlight.neolight;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;

/**
 * A simple fade transition effect between scenes.
 */
public class FadeTransition implements SceneTransition {
    private static final float FADE_SPEED = 2.0f; // Fade speed in alpha units per second
    private float alpha;
    private boolean fadingIn;
    private Runnable onComplete;
    private boolean isComplete;

    public FadeTransition() {
        this.alpha = 0.0f;
        this.fadingIn = true;
        this.isComplete = false;
    }

    @Override
    public void start(Runnable onComplete) {
        this.onComplete = onComplete;
        this.alpha = 0.0f;
        this.fadingIn = true;
        this.isComplete = false;
    }

    @Override
    public void update(double deltaTime) {
        if (isComplete) {
            return;
        }

        if (fadingIn) {
            alpha += FADE_SPEED * deltaTime;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadingIn = false;
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        } else {
            alpha -= FADE_SPEED * deltaTime;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                isComplete = true;
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        if (alpha <= 0.0f) {
            return;
        }

        // Save the original composite
        java.awt.Composite originalComposite = g.getComposite();
        
        // Set up the fade effect
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, g.getDeviceConfiguration().getBounds().width, 
                         g.getDeviceConfiguration().getBounds().height);
        
        // Restore the original composite
        g.setComposite(originalComposite);
    }

    @Override
    public void end(Runnable onComplete) {
        this.onComplete = onComplete;
        this.fadingIn = false;
        this.alpha = 1.0f;
    }
} 