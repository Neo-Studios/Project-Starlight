package com.neostudios.starlight;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Simple event bus for decoupled communication in NeoLight engine.
 */
public class EventBus {
    private final List<Consumer<Object>> listeners = new ArrayList<>();

    public void subscribe(Consumer<Object> listener) {
        listeners.add(listener);
    }

    public void unsubscribe(Consumer<Object> listener) {
        listeners.remove(listener);
    }

    public void post(Object event) {
        for (Consumer<Object> listener : listeners) {
            listener.accept(event);
        }
    }
}
