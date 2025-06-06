package com.neostudios.starlight.neolight;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles mouse input and maintains the current state of mouse buttons and position.
 * Implements MouseListener and MouseMotionListener for mouse events.
 */
public class MouseInputManager implements MouseListener, MouseMotionListener {
    private final Set<Integer> pressedButtons = new HashSet<>();
    private Point position = new Point(0, 0);
    private Point lastPosition = new Point(0, 0);
    private boolean isDragging = false;

    @Override
    public void mousePressed(MouseEvent e) {
        pressedButtons.add(e.getButton());
        position.setLocation(e.getPoint());
        lastPosition.setLocation(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressedButtons.remove(e.getButton());
        position.setLocation(e.getPoint());
        if (pressedButtons.isEmpty()) {
            isDragging = false;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastPosition.setLocation(position);
        position.setLocation(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        isDragging = true;
        lastPosition.setLocation(position);
        position.setLocation(e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used - we use pressed/released instead
    }

    /**
     * Checks if a specific mouse button is currently pressed.
     * @param button The mouse button to check (MouseEvent.BUTTON1, BUTTON2, or BUTTON3)
     * @return true if the button is pressed
     */
    public boolean isButtonPressed(int button) {
        return pressedButtons.contains(button);
    }

    /**
     * Gets the current mouse position.
     * @return A Point containing the current mouse coordinates
     */
    public Point getPosition() {
        return new Point(position);
    }

    /**
     * Gets the previous mouse position.
     * @return A Point containing the previous mouse coordinates
     */
    public Point getLastPosition() {
        return new Point(lastPosition);
    }

    /**
     * Gets the mouse movement delta since the last update.
     * @return A Point containing the x and y movement deltas
     */
    public Point getMovementDelta() {
        return new Point(
            position.x - lastPosition.x,
            position.y - lastPosition.y
        );
    }

    /**
     * Checks if the mouse is currently being dragged.
     * @return true if the mouse is being dragged
     */
    public boolean isDragging() {
        return isDragging;
    }

    /**
     * Gets the set of currently pressed mouse buttons.
     * @return A Set of button codes (MouseEvent.BUTTON1, BUTTON2, or BUTTON3)
     */
    public Set<Integer> getPressedButtons() {
        return new HashSet<>(pressedButtons);
    }
} 