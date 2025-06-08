package com.neostudios.starlight.neolight.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.function.Consumer;

/**
 * Manages input handling in the game.
 */
public class InputSystem implements KeyListener {
    private static final Logger LOGGER = Logger.getLogger(InputSystem.class.getName());
    private static InputSystem instance;
    
    private final ConcurrentMap<Integer, Boolean> keyStates;
    private final ConcurrentMap<String, InputBinding> inputBindings;
    private final ConcurrentMap<String, Consumer<InputEvent>> inputHandlers;
    private boolean enabled;
    
    private InputSystem() {
        this.keyStates = new ConcurrentHashMap<>();
        this.inputBindings = new ConcurrentHashMap<>();
        this.inputHandlers = new ConcurrentHashMap<>();
        this.enabled = true;
    }
    
    public static synchronized InputSystem getInstance() {
        if (instance == null) {
            instance = new InputSystem();
        }
        return instance;
    }
    
    /**
     * Registers an input binding.
     * @param name Unique identifier for the binding
     * @param binding The input binding
     */
    public void registerBinding(String name, InputBinding binding) {
        inputBindings.put(name, binding);
        LOGGER.info("Registered input binding: " + name);
    }
    
    /**
     * Unregisters an input binding.
     * @param name The name of the binding to unregister
     */
    public void unregisterBinding(String name) {
        inputBindings.remove(name);
        LOGGER.info("Unregistered input binding: " + name);
    }
    
    /**
     * Registers an input handler.
     * @param name Unique identifier for the handler
     * @param handler The input handler
     */
    public void registerHandler(String name, Consumer<InputEvent> handler) {
        inputHandlers.put(name, handler);
        LOGGER.info("Registered input handler: " + name);
    }
    
    /**
     * Unregisters an input handler.
     * @param name The name of the handler to unregister
     */
    public void unregisterHandler(String name) {
        inputHandlers.remove(name);
        LOGGER.info("Unregistered input handler: " + name);
    }
    
    /**
     * Updates the input system.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        // Check all input bindings
        for (InputBinding binding : inputBindings.values()) {
            if (binding.isTriggered(keyStates)) {
                InputEvent event = new InputEvent(binding.getName(), deltaTime);
                for (Consumer<InputEvent> handler : inputHandlers.values()) {
                    handler.accept(event);
                }
            }
        }
    }
    
    /**
     * Checks if a key is currently pressed.
     * @param keyCode The key code to check
     * @return true if the key is pressed
     */
    public boolean isKeyPressed(int keyCode) {
        return keyStates.getOrDefault(keyCode, false);
    }
    
    /**
     * Enables or disables the input system.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            keyStates.clear();
        }
    }
    
    /**
     * Clears all input bindings and handlers.
     */
    public void clear() {
        inputBindings.clear();
        inputHandlers.clear();
        keyStates.clear();
        LOGGER.info("Cleared all input bindings and handlers");
    }
    
    // KeyListener implementation
    @Override
    public void keyPressed(KeyEvent e) {
        if (!enabled) return;
        keyStates.put(e.getKeyCode(), true);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (!enabled) return;
        keyStates.put(e.getKeyCode(), false);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    /**
     * Represents an input binding.
     */
    public static class InputBinding {
        private final String name;
        private final int[] keyCodes;
        private final InputType type;
        private final long[] keyTimestamps;
        private static final long SEQUENCE_TIMEOUT = 1000; // 1 second timeout for sequence
        
        public InputBinding(String name, InputType type, int... keyCodes) {
            this.name = name;
            this.type = type;
            this.keyCodes = keyCodes;
            this.keyTimestamps = new long[keyCodes.length];
            // Initialize timestamps to -1 to indicate not pressed
            for (int i = 0; i < keyTimestamps.length; i++) {
                keyTimestamps[i] = -1;
            }
        }
        
        public boolean isTriggered(ConcurrentMap<Integer, Boolean> keyStates) {
            switch (type) {
                case ANY:
                    for (int keyCode : keyCodes) {
                        if (keyStates.getOrDefault(keyCode, false)) {
                            return true;
                        }
                    }
                    return false;
                    
                case ALL:
                    for (int keyCode : keyCodes) {
                        if (!keyStates.getOrDefault(keyCode, false)) {
                            return false;
                        }
                    }
                    return keyCodes.length > 0;
                    
                case SEQUENCE:
                    long currentTime = System.currentTimeMillis();
                    boolean sequenceDetected = true;
                    
                    // Update timestamps for currently pressed keys
                    for (int i = 0; i < keyCodes.length; i++) {
                        if (keyStates.getOrDefault(keyCodes[i], false)) {
                            if (keyTimestamps[i] == -1) {
                                // Key was just pressed
                                keyTimestamps[i] = currentTime;
                            }
                        } else {
                            // Key is not pressed, reset its timestamp
                            keyTimestamps[i] = -1;
                        }
                    }
                    
                    // Check if sequence is valid
                    for (int i = 0; i < keyCodes.length; i++) {
                        if (keyTimestamps[i] == -1) {
                            // Key in sequence not pressed
                            sequenceDetected = false;
                            break;
                        }
                        
                        if (i > 0) {
                            // Check if keys are pressed in order and within timeout
                            if (keyTimestamps[i] < keyTimestamps[i-1] || 
                                keyTimestamps[i] - keyTimestamps[i-1] > SEQUENCE_TIMEOUT) {
                                sequenceDetected = false;
                                break;
                            }
                        }
                    }
                    
                    // Reset sequence if detected or if timeout occurred
                    if (sequenceDetected || 
                        (currentTime - keyTimestamps[0] > SEQUENCE_TIMEOUT && keyTimestamps[0] != -1)) {
                        for (int i = 0; i < keyTimestamps.length; i++) {
                            keyTimestamps[i] = -1;
                        }
                    }
                    
                    return sequenceDetected;
                    
                default:
                    return false;
            }
        }
        
        public String getName() {
            return name;
        }
        
        public InputType getType() {
            return type;
        }
        
        public int[] getKeyCodes() {
            return keyCodes;
        }
    }
    
    /**
     * Represents an input event.
     */
    public static class InputEvent {
        private final String bindingName;
        private final double deltaTime;
        
        public InputEvent(String bindingName, double deltaTime) {
            this.bindingName = bindingName;
            this.deltaTime = deltaTime;
        }
        
        public String getBindingName() {
            return bindingName;
        }
        
        public double getDeltaTime() {
            return deltaTime;
        }
    }
    
    /**
     * Types of input bindings.
     */
    public enum InputType {
        /**
         * Any of the bound keys must be pressed.
         */
        ANY,
        
        /**
         * All bound keys must be pressed.
         */
        ALL,
        
        /**
         * Keys must be pressed in sequence.
         */
        SEQUENCE
    }
    
    /**
     * Common key codes for convenience.
     */
    public static class Keys {
        public static final int UP = KeyEvent.VK_UP;
        public static final int DOWN = KeyEvent.VK_DOWN;
        public static final int LEFT = KeyEvent.VK_LEFT;
        public static final int RIGHT = KeyEvent.VK_RIGHT;
        public static final int SPACE = KeyEvent.VK_SPACE;
        public static final int ENTER = KeyEvent.VK_ENTER;
        public static final int ESCAPE = KeyEvent.VK_ESCAPE;
        public static final int SHIFT = KeyEvent.VK_SHIFT;
        public static final int CONTROL = KeyEvent.VK_CONTROL;
        public static final int ALT = KeyEvent.VK_ALT;
        public static final int A = KeyEvent.VK_A;
        public static final int B = KeyEvent.VK_B;
        public static final int C = KeyEvent.VK_C;
        public static final int D = KeyEvent.VK_D;
        public static final int E = KeyEvent.VK_E;
        public static final int F = KeyEvent.VK_F;
        public static final int G = KeyEvent.VK_G;
        public static final int H = KeyEvent.VK_H;
        public static final int I = KeyEvent.VK_I;
        public static final int J = KeyEvent.VK_J;
        public static final int K = KeyEvent.VK_K;
        public static final int L = KeyEvent.VK_L;
        public static final int M = KeyEvent.VK_M;
        public static final int N = KeyEvent.VK_N;
        public static final int O = KeyEvent.VK_O;
        public static final int P = KeyEvent.VK_P;
        public static final int Q = KeyEvent.VK_Q;
        public static final int R = KeyEvent.VK_R;
        public static final int S = KeyEvent.VK_S;
        public static final int T = KeyEvent.VK_T;
        public static final int U = KeyEvent.VK_U;
        public static final int V = KeyEvent.VK_V;
        public static final int W = KeyEvent.VK_W;
        public static final int X = KeyEvent.VK_X;
        public static final int Y = KeyEvent.VK_Y;
        public static final int Z = KeyEvent.VK_Z;
        public static final int NUM_0 = KeyEvent.VK_0;
        public static final int NUM_1 = KeyEvent.VK_1;
        public static final int NUM_2 = KeyEvent.VK_2;
        public static final int NUM_3 = KeyEvent.VK_3;
        public static final int NUM_4 = KeyEvent.VK_4;
        public static final int NUM_5 = KeyEvent.VK_5;
        public static final int NUM_6 = KeyEvent.VK_6;
        public static final int NUM_7 = KeyEvent.VK_7;
        public static final int NUM_8 = KeyEvent.VK_8;
        public static final int NUM_9 = KeyEvent.VK_9;
        public static final int F1 = KeyEvent.VK_F1;
        public static final int F2 = KeyEvent.VK_F2;
        public static final int F3 = KeyEvent.VK_F3;
        public static final int F4 = KeyEvent.VK_F4;
        public static final int F5 = KeyEvent.VK_F5;
        public static final int F6 = KeyEvent.VK_F6;
        public static final int F7 = KeyEvent.VK_F7;
        public static final int F8 = KeyEvent.VK_F8;
        public static final int F9 = KeyEvent.VK_F9;
        public static final int F10 = KeyEvent.VK_F10;
        public static final int F11 = KeyEvent.VK_F11;
        public static final int F12 = KeyEvent.VK_F12;
    }
} 