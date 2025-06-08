// This file is a duplicate. The real AssetManager is in the neolight package.

package com.neostudios.starlight;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AssetManager {
    private static final Logger LOGGER = Logger.getLogger(AssetManager.class.getName());
    private static AssetManager instance;
    
    private final Map<String, BufferedImage> imageCache;
    private final Map<String, Clip> audioCache;
    private final Map<String, Object> genericCache;
    
    private AssetManager() {
        imageCache = new ConcurrentHashMap<>();
        audioCache = new ConcurrentHashMap<>();
        genericCache = new ConcurrentHashMap<>();
    }
    
    public static synchronized AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    public CompletableFuture<BufferedImage> loadImageAsync(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (imageCache.containsKey(path)) {
                    return imageCache.get(path);
                }
                
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is == null) {
                        throw new IOException("Resource not found: " + path);
                    }
                    BufferedImage image = ImageIO.read(is);
                    imageCache.put(path, image);
                    return image;
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to load image: " + path, e);
                throw new RuntimeException("Failed to load image: " + path, e);
            }
        });
    }
    
    public CompletableFuture<Clip> loadAudioAsync(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (audioCache.containsKey(path)) {
                    return audioCache.get(path);
                }
                
                try (InputStream is = getClass().getResourceAsStream(path)) {
                    if (is == null) {
                        throw new IOException("Resource not found: " + path);
                    }
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(is);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    audioCache.put(path, clip);
                    return clip;
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to load audio: " + path, e);
                throw new RuntimeException("Failed to load audio: " + path, e);
            }
        });
    }
    
    public <T> void cacheObject(String key, T object) {
        genericCache.put(key, object);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getCachedObject(String key) {
        return (T) genericCache.get(key);
    }
    
    public void clearImageCache() {
        imageCache.clear();
    }
    
    public void clearAudioCache() {
        audioCache.values().forEach(Clip::close);
        audioCache.clear();
    }
    
    public void clearAllCaches() {
        clearImageCache();
        clearAudioCache();
        genericCache.clear();
    }
    
    public boolean isImageLoaded(String path) {
        return imageCache.containsKey(path);
    }
    
    public boolean isAudioLoaded(String path) {
        return audioCache.containsKey(path);
    }
    
    public void preloadAssets(String[] imagePaths, String[] audioPaths) {
        CompletableFuture<?>[] imageFutures = new CompletableFuture[imagePaths.length];
        CompletableFuture<?>[] audioFutures = new CompletableFuture[audioPaths.length];
        
        for (int i = 0; i < imagePaths.length; i++) {
            imageFutures[i] = loadImageAsync(imagePaths[i]);
        }
        
        for (int i = 0; i < audioPaths.length; i++) {
            audioFutures[i] = loadAudioAsync(audioPaths[i]);
        }
        
        CompletableFuture.allOf(
            CompletableFuture.allOf(imageFutures),
            CompletableFuture.allOf(audioFutures)
        ).join();
    }
}
