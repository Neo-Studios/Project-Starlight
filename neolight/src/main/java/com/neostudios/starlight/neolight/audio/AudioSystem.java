package com.neostudios.starlight.neolight.audio;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Manages audio playback in the game.
 */
public class AudioSystem {
    private static final Logger LOGGER = Logger.getLogger(AudioSystem.class.getName());
    private static AudioSystem instance;
    
    private final ConcurrentMap<String, Clip> soundEffects;
    private final ConcurrentMap<String, Clip> musicTracks;
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;
    private boolean enabled;
    
    private AudioSystem() {
        this.soundEffects = new ConcurrentHashMap<>();
        this.musicTracks = new ConcurrentHashMap<>();
        this.masterVolume = 1.0f;
        this.musicVolume = 1.0f;
        this.sfxVolume = 1.0f;
        this.enabled = true;
    }
    
    public static synchronized AudioSystem getInstance() {
        if (instance == null) {
            instance = new AudioSystem();
        }
        return instance;
    }
    
    /**
     * Loads a sound effect from a resource URL.
     * @param name Unique identifier for the sound effect
     * @param resourceUrl URL to the audio resource
     * @return true if loading was successful
     */
    public boolean loadSoundEffect(String name, URL resourceUrl) {
        try {
            AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(resourceUrl);
            Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioIn);
            soundEffects.put(name, clip);
            LOGGER.info("Loaded sound effect: " + name);
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOGGER.log(Level.SEVERE, "Failed to load sound effect: " + name, e);
            return false;
        }
    }
    
    /**
     * Loads a music track from a resource URL.
     * @param name Unique identifier for the music track
     * @param resourceUrl URL to the audio resource
     * @return true if loading was successful
     */
    public boolean loadMusic(String name, URL resourceUrl) {
        try {
            AudioInputStream audioIn = javax.sound.sampled.AudioSystem.getAudioInputStream(resourceUrl);
            Clip clip = javax.sound.sampled.AudioSystem.getClip();
            clip.open(audioIn);
            musicTracks.put(name, clip);
            LOGGER.info("Loaded music track: " + name);
            return true;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOGGER.log(Level.SEVERE, "Failed to load music track: " + name, e);
            return false;
        }
    }
    
    /**
     * Plays a sound effect.
     * @param name Name of the sound effect to play
     * @param loop Whether to loop the sound effect
     */
    public void playSoundEffect(String name, boolean loop) {
        if (!enabled) return;
        
        Clip clip = soundEffects.get(name);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.setLoopPoints(0, -1);
            clip.loop(loop ? Clip.LOOP_CONTINUOUSLY : 0);
            
            // Set volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = masterVolume * sfxVolume;
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    /**
     * Plays a music track.
     * @param name Name of the music track to play
     * @param loop Whether to loop the music track
     */
    public void playMusic(String name, boolean loop) {
        if (!enabled) return;
        
        // Stop any currently playing music
        stopAllMusic();
        
        Clip clip = musicTracks.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.setLoopPoints(0, -1);
            clip.loop(loop ? Clip.LOOP_CONTINUOUSLY : 0);
            
            // Set volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = masterVolume * musicVolume;
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
    
    /**
     * Stops a specific sound effect.
     * @param name Name of the sound effect to stop
     */
    public void stopSoundEffect(String name) {
        Clip clip = soundEffects.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    /**
     * Stops a specific music track.
     * @param name Name of the music track to stop
     */
    public void stopMusic(String name) {
        Clip clip = musicTracks.get(name);
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    /**
     * Stops all sound effects.
     */
    public void stopAllSoundEffects() {
        for (Clip clip : soundEffects.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    /**
     * Stops all music tracks.
     */
    public void stopAllMusic() {
        for (Clip clip : musicTracks.values()) {
            if (clip.isRunning()) {
                clip.stop();
            }
        }
    }
    
    /**
     * Sets the master volume.
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateAllVolumes();
    }
    
    /**
     * Sets the music volume.
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateMusicVolumes();
    }
    
    /**
     * Sets the sound effects volume.
     * @param volume Volume level (0.0 to 1.0)
     */
    public void setSfxVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
        updateSfxVolumes();
    }
    
    /**
     * Enables or disables the audio system.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stopAllSoundEffects();
            stopAllMusic();
        }
    }
    
    /**
     * Cleans up resources.
     */
    public void cleanup() {
        stopAllSoundEffects();
        stopAllMusic();
        
        for (Clip clip : soundEffects.values()) {
            clip.close();
        }
        for (Clip clip : musicTracks.values()) {
            clip.close();
        }
        
        soundEffects.clear();
        musicTracks.clear();
    }
    
    private void updateAllVolumes() {
        updateMusicVolumes();
        updateSfxVolumes();
    }
    
    private void updateMusicVolumes() {
        float volume = masterVolume * musicVolume;
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        
        for (Clip clip : musicTracks.values()) {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(dB);
            }
        }
    }
    
    private void updateSfxVolumes() {
        float volume = masterVolume * sfxVolume;
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        
        for (Clip clip : soundEffects.values()) {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(dB);
            }
        }
    }
} 