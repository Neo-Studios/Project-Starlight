package com.neostudios.starlight.neolight.input;

import net.java.games.input.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Handles gamepad input using JInput.
 */
public class GamepadHandler {
    private static final Logger LOGGER = Logger.getLogger(GamepadHandler.class.getName());
    private static GamepadHandler instance;
    
    private final Controller[] controllers;
    private final ConcurrentMap<String, Float> buttonStates;
    private final ConcurrentMap<String, Float> axisStates;
    private boolean enabled;
    
    private GamepadHandler() {
        this.controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        this.buttonStates = new ConcurrentHashMap<>();
        this.axisStates = new ConcurrentHashMap<>();
        this.enabled = true;
        
        // Log available controllers
        for (Controller controller : controllers) {
            LOGGER.info("Found controller: " + controller.getName() + " (" + controller.getType() + ")");
        }
    }
    
    public static synchronized GamepadHandler getInstance() {
        if (instance == null) {
            instance = new GamepadHandler();
        }
        return instance;
    }
    
    /**
     * Updates the gamepad states.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        for (Controller controller : controllers) {
            if (!controller.poll()) {
                continue;
            }
            
            // Update button states
            for (Component component : controller.getComponents()) {
                if (component.isAnalog()) {
                    float value = component.getPollData();
                    // Apply deadzone
                    if (Math.abs(value) < 0.1f) {
                        value = 0.0f;
                    }
                    axisStates.put(component.getIdentifier().getName(), value);
                } else {
                    buttonStates.put(component.getIdentifier().getName(), component.getPollData());
                }
            }
        }
    }
    
    /**
     * Gets the state of a button.
     * @param buttonName The name of the button
     * @return The button state (0.0 to 1.0)
     */
    public float getButtonState(String buttonName) {
        return buttonStates.getOrDefault(buttonName, 0.0f);
    }
    
    /**
     * Gets the state of an axis.
     * @param axisName The name of the axis
     * @return The axis state (-1.0 to 1.0)
     */
    public float getAxisState(String axisName) {
        return axisStates.getOrDefault(axisName, 0.0f);
    }
    
    /**
     * Checks if a button is pressed.
     * @param buttonName The name of the button
     * @return true if the button is pressed
     */
    public boolean isButtonPressed(String buttonName) {
        return buttonStates.getOrDefault(buttonName, 0.0f) > 0.5f;
    }
    
    /**
     * Gets the number of connected controllers.
     * @return The number of controllers
     */
    public int getControllerCount() {
        return controllers.length;
    }
    
    /**
     * Gets a controller by index.
     * @param index The controller index
     * @return The controller, or null if not found
     */
    public Controller getController(int index) {
        if (index >= 0 && index < controllers.length) {
            return controllers[index];
        }
        return null;
    }
    
    /**
     * Enables or disables the gamepad handler.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            buttonStates.clear();
            axisStates.clear();
        }
    }
    
    /**
     * Common button and axis names for convenience.
     */
    public static class Buttons {
        // Xbox/PlayStation style buttons
        public static final String A = "0";
        public static final String B = "1";
        public static final String X = "2";
        public static final String Y = "3";
        public static final String LB = "4";
        public static final String RB = "5";
        public static final String BACK = "6";
        public static final String START = "7";
        public static final String L3 = "8";
        public static final String R3 = "9";
        
        // D-pad
        public static final String DPAD_UP = "dpad_up";
        public static final String DPAD_DOWN = "dpad_down";
        public static final String DPAD_LEFT = "dpad_left";
        public static final String DPAD_RIGHT = "dpad_right";
    }
    
    public static class Axes {
        // Left stick
        public static final String LEFT_X = "x";
        public static final String LEFT_Y = "y";
        
        // Right stick
        public static final String RIGHT_X = "rx";
        public static final String RIGHT_Y = "ry";
        
        // Triggers
        public static final String LT = "z";
        public static final String RT = "rz";
    }
    
    /**
     * Creates an input binding for a gamepad button.
     * @param name The binding name
     * @param buttonName The button name
     * @return The input binding
     */
    public static InputSystem.InputBinding createButtonBinding(String name, String buttonName) {
        return new InputSystem.InputBinding(name, InputSystem.InputType.ANY) {
            @Override
            public boolean isTriggered(ConcurrentMap<Integer, Boolean> keyStates) {
                return GamepadHandler.getInstance().isButtonPressed(buttonName);
            }
        };
    }
    
    /**
     * Creates an input binding for a gamepad axis.
     * @param name The binding name
     * @param axisName The axis name
     * @param threshold The threshold value (-1.0 to 1.0)
     * @param positive Whether to check for positive or negative values
     * @return The input binding
     */
    public static InputSystem.InputBinding createAxisBinding(String name, String axisName, float threshold, boolean positive) {
        return new InputSystem.InputBinding(name, InputSystem.InputType.ANY) {
            @Override
            public boolean isTriggered(ConcurrentMap<Integer, Boolean> keyStates) {
                float value = GamepadHandler.getInstance().getAxisState(axisName);
                return positive ? value > threshold : value < -threshold;
            }
        };
    }
} 