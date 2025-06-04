package com.neostudios.starlight;

public class Player {
    private final String name;
    private int health;
    private int score;

    public static final int DEFAULT_HEALTH = 100;
    public static final int DEFAULT_SCORE = 0;

    public Player(String name) {
        this.name = name;
        this.health = DEFAULT_HEALTH;
        this.score = DEFAULT_SCORE;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public void addScore(int amount) {
        this.score += amount;
    }
}
