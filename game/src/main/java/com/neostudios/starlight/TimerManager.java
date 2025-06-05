package com.neostudios.starlight;

/**
 * TimerManager for scheduling delayed and repeated tasks in NeoLight engine.
 */
import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private final Timer timer = new Timer();

    public void schedule(Runnable task, long delayMs) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delayMs);
    }

    public void scheduleAtFixedRate(Runnable task, long delayMs, long periodMs) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delayMs, periodMs);
    }

    public void shutdown() {
        timer.cancel();
    }
}
