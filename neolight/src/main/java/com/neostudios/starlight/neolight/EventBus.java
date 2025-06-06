package com.neostudios.starlight.neolight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Simple event bus for decoupled communication between engine/game systems.
 */
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public <T> void publish(T event) {
        List<Consumer<?>> eventListeners = listeners.getOrDefault(event.getClass(), Collections.emptyList());
        for (Consumer<?> listener : eventListeners) {
            @SuppressWarnings("unchecked")
            Consumer<T> typedListener = (Consumer<T>) listener;
            typedListener.accept(event);
        }
    }
}
