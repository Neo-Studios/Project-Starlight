package com.neostudios.starlight;

import com.neostudios.starlight.neolight.AssetManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() {
        assertTrue(true);
    }

    @Test
    void testPlayerInitialization() {
        Player player = new Player("TestPlayer");
        assertEquals("TestPlayer", player.getName());
        assertEquals(Player.DEFAULT_HEALTH, player.getHealth());
        assertEquals(Player.DEFAULT_SCORE, player.getScore());
    }

    @Test
    void testPlayerTakeDamage() {
        Player player = new Player("TestPlayer");
        player.takeDamage(30);
        assertEquals(Player.DEFAULT_HEALTH - 30, player.getHealth());
        player.takeDamage(100);
        assertEquals(0, player.getHealth());
    }

    @Test
    void testPlayerAddScore() {
        Player player = new Player("TestPlayer");
        player.addScore(50);
        assertEquals(50, player.getScore());
    }

    @Test
    void testGameStateEnum() {
        assertNotNull(GameState.valueOf("MENU"));
        assertNotNull(GameState.valueOf("PLAYING"));
        assertNotNull(GameState.valueOf("PAUSED"));
        assertNotNull(GameState.valueOf("GAME_OVER"));
    }

    @Test
    void testConfigDefaults() {
        App app = new App();
        assertEquals(200, app.getPlayerX()); // Default from config or constant
        assertEquals(200, app.getPlayerY());
    }
}
