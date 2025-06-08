package com.neostudios.starlight.neolight;

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

/**
 * Manages game assets with async loading and caching.
 */
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
    
    /**
     * Gets the singleton instance of AssetManager.
     * @return The AssetManager instance
     */
    public static synchronized AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    /**
     * Loads an image asynchronously.
     * @param path The path to the image resource
     * @return A CompletableFuture that will complete with the loaded image
     */
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
    
    /**
     * Loads an audio clip asynchronously.
     * @param path The path to the audio resource
     * @return A CompletableFuture that will complete with the loaded audio clip
     */
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
    
    /**
     * Caches a generic object.
     * @param key The cache key
     * @param object The object to cache
     */
    public <T> void cacheObject(String key, T object) {
        genericCache.put(key, object);
    }
    
    /**
     * Gets a cached object.
     * @param key The cache key
     * @return The cached object, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getCachedObject(String key) {
        return (T) genericCache.get(key);
    }
    
    /**
     * Clears the image cache.
     */
    public void clearImageCache() {
        imageCache.clear();
    }
    
    /**
     * Clears the audio cache.
     */
    public void clearAudioCache() {
        audioCache.values().forEach(Clip::close);
        audioCache.clear();
    }
    
    /**
     * Clears all caches.
     */
    public void clearAllCaches() {
        clearImageCache();
        clearAudioCache();
        genericCache.clear();
    }
    
    /**
     * Checks if an image is loaded.
     * @param path The path to check
     * @return true if the image is loaded, false otherwise
     */
    public boolean isImageLoaded(String path) {
        return imageCache.containsKey(path);
    }
    
    /**
     * Checks if an audio clip is loaded.
     * @param path The path to check
     * @return true if the audio clip is loaded, false otherwise
     */
    public boolean isAudioLoaded(String path) {
        return audioCache.containsKey(path);
    }
    
    /**
     * Preloads multiple assets.
     * @param imagePaths Array of image paths to preload
     * @param audioPaths Array of audio paths to preload
     * @return A CompletableFuture that completes when all assets are loaded
     */
    public CompletableFuture<Void> preloadAssets(String[] imagePaths, String[] audioPaths) {
        CompletableFuture<?>[] imageFutures = new CompletableFuture[imagePaths.length];
        CompletableFuture<?>[] audioFutures = new CompletableFuture[audioPaths.length];
        
        for (int i = 0; i < imagePaths.length; i++) {
            imageFutures[i] = loadImageAsync(imagePaths[i]);
        }
        
        for (int i = 0; i < audioPaths.length; i++) {
            audioFutures[i] = loadAudioAsync(audioPaths[i]);
        }
        
        return CompletableFuture.allOf(
            CompletableFuture.allOf(imageFutures),
            CompletableFuture.allOf(audioFutures)
        );
    }
}
