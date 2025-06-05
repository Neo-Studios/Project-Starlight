package com.neostudios.starlight.neolight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Simple event bus for decoupled communication between engine/game components.
 */
public class EventBus {
    private final Map<Class<?>, Set<Consumer<?>>> listeners = new HashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new HashSet<>()).add(listener);
    }

    public <T> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        Set<Consumer<?>> set = listeners.get(eventType);
        if (set != null) set.remove(listener);
    }

    public <T> void publish(T event) {
        Set<Consumer<?>> set = listeners.get(event.getClass());
        if (set != null) {
            for (Consumer<?> consumer : set) {
                @SuppressWarnings("unchecked")
                Consumer<T> c = (Consumer<T>) consumer;
                c.accept(event);
            }
        }
    }
}
