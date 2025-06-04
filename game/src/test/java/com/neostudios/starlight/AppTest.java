package com.neostudios.starlight;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
