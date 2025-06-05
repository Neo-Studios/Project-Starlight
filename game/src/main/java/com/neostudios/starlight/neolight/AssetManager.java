package com.neostudios.starlight.neolight;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * AssetManager handles loading and caching of images and audio resources.
 */
public class AssetManager {
    private final Map<String, java.awt.image.BufferedImage> imageCache = new HashMap<>();
    private final Map<String, Clip> audioCache = new HashMap<>();
    private final ClassLoader loader = getClass().getClassLoader();

    public java.awt.image.BufferedImage getImage(String path) {
        if (imageCache.containsKey(path)) return imageCache.get(path);
        try {
            URL url = loader.getResource(path);
            if (url != null) {
                java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(url);
                imageCache.put(path, img);
                return img;
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("[AssetManager] Failed to load image: " + path);
        }
        return null;
    }

    public Clip getAudio(String path) {
        if (audioCache.containsKey(path)) return audioCache.get(path);
        try {
            URL url = loader.getResource(path);
            if (url != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                audioCache.put(path, clip);
                return clip;
            }
        } catch (IOException | IllegalArgumentException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.err.println("[AssetManager] Failed to load audio: " + path);
        }
        return null;
    }

    public void clear() {
        imageCache.clear();
        for (Clip clip : audioCache.values()) {
            clip.close();
        }
        audioCache.clear();
    }
}
