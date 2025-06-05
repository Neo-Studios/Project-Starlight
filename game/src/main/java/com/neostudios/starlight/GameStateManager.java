package com.neostudios.starlight;

/**
 * Manages transitions and logic between different game states.
 */
public class GameStateManager {
    private GameState currentState;

    public GameStateManager() {
        this.currentState = GameState.MENU;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setState(GameState newState) {
        this.currentState = newState;
    }
}
