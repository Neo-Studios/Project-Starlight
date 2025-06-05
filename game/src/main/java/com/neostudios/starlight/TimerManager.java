package com.neostudios.starlight.neolight;

import java.util.HashSet;
import java.util.Set;

/**
 * TimerManager allows scheduling of delayed or repeated tasks.
 */
public class TimerManager {
    private final Set<TimerTask> tasks = new HashSet<>();

    public void update(double deltaTime) {
        Set<TimerTask> finished = new HashSet<>();
        for (TimerTask task : tasks) {
            task.elapsed += deltaTime;
            if (task.elapsed >= task.delay) {
                task.runnable.run();
                if (task.repeat) {
                    task.elapsed = 0;
                } else {
                    finished.add(task);
                }
            }
        }
        tasks.removeAll(finished);
    }

    public void schedule(Runnable runnable, double delay, boolean repeat) {
        tasks.add(new TimerTask(runnable, delay, repeat));
    }

    private static class TimerTask {
        final Runnable runnable;
        final double delay;
        final boolean repeat;
        double elapsed = 0;
        TimerTask(Runnable r, double d, boolean rep) {
            runnable = r; delay = d; repeat = rep;
        }
    }
}

// This file is a duplicate. The real TimerManager is in the neolight package.
