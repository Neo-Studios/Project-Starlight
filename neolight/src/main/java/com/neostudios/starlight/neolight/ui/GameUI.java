package com.neostudios.starlight.neolight.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Manages UI elements in the game.
 */
public class GameUI {
    private static final Logger LOGGER = Logger.getLogger(GameUI.class.getName());
    private static GameUI instance;
    
    private final ConcurrentMap<String, UIElement> elements;
    private final List<UIElement> activeElements;
    private UIElement focusedElement;
    private boolean enabled;
    
    private GameUI() {
        this.elements = new ConcurrentHashMap<>();
        this.activeElements = new ArrayList<>();
        this.focusedElement = null;
        this.enabled = true;
    }
    
    public static synchronized GameUI getInstance() {
        if (instance == null) {
            instance = new GameUI();
        }
        return instance;
    }
    
    /**
     * Adds a UI element to the system.
     * @param element The UI element to add
     */
    public void addElement(UIElement element) {
        elements.put(element.getId(), element);
        if (element.isVisible()) {
            activeElements.add(element);
        }
        LOGGER.info("Added UI element: " + element.getId());
    }
    
    /**
     * Removes a UI element from the system.
     * @param elementId The ID of the element to remove
     */
    public void removeElement(String elementId) {
        UIElement element = elements.remove(elementId);
        if (element != null) {
            activeElements.remove(element);
            if (focusedElement == element) {
                focusedElement = null;
            }
            LOGGER.info("Removed UI element: " + elementId);
        }
    }
    
    /**
     * Updates all active UI elements.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        for (UIElement element : activeElements) {
            element.update(deltaTime);
        }
    }
    
    /**
     * Renders all active UI elements.
     * @param g The graphics context
     */
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        // Sort elements by layer before rendering
        activeElements.sort((a, b) -> Integer.compare(a.getLayer(), b.getLayer()));
        
        for (UIElement element : activeElements) {
            element.render(g);
        }
    }
    
    /**
     * Handles mouse movement events.
     * @param e The mouse event
     */
    public void handleMouseMoved(MouseEvent e) {
        if (!enabled) return;
        
        for (UIElement element : activeElements) {
            if (element.contains(e.getX(), e.getY())) {
                element.onMouseEnter();
            } else {
                element.onMouseExit();
            }
        }
    }
    
    /**
     * Handles mouse click events.
     * @param e The mouse event
     */
    public void handleMouseClicked(MouseEvent e) {
        if (!enabled) return;
        
        for (UIElement element : activeElements) {
            if (element.contains(e.getX(), e.getY())) {
                element.onClick();
                focusedElement = element;
                break;
            }
        }
    }
    
    /**
     * Handles mouse press events.
     * @param e The mouse event
     */
    public void handleMousePressed(MouseEvent e) {
        if (!enabled) return;
        
        for (UIElement element : activeElements) {
            if (element.contains(e.getX(), e.getY())) {
                element.onMouseDown();
                break;
            }
        }
    }
    
    /**
     * Handles mouse release events.
     * @param e The mouse event
     */
    public void handleMouseReleased(MouseEvent e) {
        if (!enabled) return;
        
        for (UIElement element : activeElements) {
            if (element.contains(e.getX(), e.getY())) {
                element.onMouseUp();
                break;
            }
        }
    }
    
    /**
     * Shows a UI element.
     * @param elementId The ID of the element to show
     */
    public void showElement(String elementId) {
        UIElement element = elements.get(elementId);
        if (element != null && !element.isVisible()) {
            element.setVisible(true);
            activeElements.add(element);
            LOGGER.info("Showed UI element: " + elementId);
        }
    }
    
    /**
     * Hides a UI element.
     * @param elementId The ID of the element to hide
     */
    public void hideElement(String elementId) {
        UIElement element = elements.get(elementId);
        if (element != null && element.isVisible()) {
            element.setVisible(false);
            activeElements.remove(element);
            if (focusedElement == element) {
                focusedElement = null;
            }
            LOGGER.info("Hid UI element: " + elementId);
        }
    }
    
    /**
     * Gets a UI element by ID.
     * @param elementId The ID of the element to get
     * @return The UI element, or null if not found
     */
    public UIElement getElement(String elementId) {
        return elements.get(elementId);
    }
    
    /**
     * Enables or disables the UI system.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Clears all UI elements.
     */
    public void clear() {
        elements.clear();
        activeElements.clear();
        focusedElement = null;
        LOGGER.info("Cleared all UI elements");
    }
    
    /**
     * Base class for all UI elements.
     */
    public static abstract class UIElement {
        private final String id;
        private int x, y;
        private int width, height;
        private boolean visible;
        private int layer;
        private Color backgroundColor;
        private Color foregroundColor;
        private boolean hovered;
        private boolean pressed;
        
        public UIElement(String id, int x, int y, int width, int height) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.visible = true;
            this.layer = 0;
            this.backgroundColor = Color.WHITE;
            this.foregroundColor = Color.BLACK;
            this.hovered = false;
            this.pressed = false;
        }
        
        public void update(double deltaTime) {
            // Override in subclasses
        }
        
        public abstract void render(Graphics2D g);
        
        public boolean contains(int mouseX, int mouseY) {
            return mouseX >= x && mouseX <= x + width &&
                   mouseY >= y && mouseY <= y + height;
        }
        
        public void onMouseEnter() {
            hovered = true;
        }
        
        public void onMouseExit() {
            hovered = false;
            pressed = false;
        }
        
        public void onMouseDown() {
            pressed = true;
        }
        
        public void onMouseUp() {
            pressed = false;
        }
        
        public void onClick() {
            // Override in subclasses
        }
        
        // Getters and setters
        public String getId() { return id; }
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        public boolean isVisible() { return visible; }
        public void setVisible(boolean visible) { this.visible = visible; }
        public int getLayer() { return layer; }
        public void setLayer(int layer) { this.layer = layer; }
        public Color getBackgroundColor() { return backgroundColor; }
        public void setBackgroundColor(Color backgroundColor) { this.backgroundColor = backgroundColor; }
        public Color getForegroundColor() { return foregroundColor; }
        public void setForegroundColor(Color foregroundColor) { this.foregroundColor = foregroundColor; }
        public boolean isHovered() { return hovered; }
        public boolean isPressed() { return pressed; }
    }
    
    /**
     * A button UI element.
     */
    public static class Button extends UIElement {
        private String text;
        private Font font;
        private Color hoverColor;
        private Color pressedColor;
        private Runnable onClickAction;
        
        public Button(String id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height);
            this.text = text;
            this.font = new Font("Arial", Font.PLAIN, 14);
            this.hoverColor = new Color(200, 200, 200);
            this.pressedColor = new Color(180, 180, 180);
            this.onClickAction = null;
        }
        
        @Override
        public void render(Graphics2D g) {
            // Draw background
            if (isPressed()) {
                g.setColor(pressedColor);
            } else if (isHovered()) {
                g.setColor(hoverColor);
            } else {
                g.setColor(getBackgroundColor());
            }
            g.fillRect(getX(), getY(), getWidth(), getHeight());
            
            // Draw border
            g.setColor(getForegroundColor());
            g.drawRect(getX(), getY(), getWidth(), getHeight());
            
            // Draw text
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics();
            int textX = getX() + (getWidth() - metrics.stringWidth(text)) / 2;
            int textY = getY() + ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(text, textX, textY);
        }
        
        @Override
        public void onClick() {
            if (onClickAction != null) {
                onClickAction.run();
            }
        }
        
        public void setOnClickAction(Runnable action) {
            this.onClickAction = action;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public void setFont(Font font) {
            this.font = font;
        }
        
        public void setHoverColor(Color hoverColor) {
            this.hoverColor = hoverColor;
        }
        
        public void setPressedColor(Color pressedColor) {
            this.pressedColor = pressedColor;
        }
    }
    
    /**
     * A label UI element.
     */
    public static class Label extends UIElement {
        private String text;
        private Font font;
        private int alignment; // 0: left, 1: center, 2: right
        
        public Label(String id, int x, int y, int width, int height, String text) {
            super(id, x, y, width, height);
            this.text = text;
            this.font = new Font("Arial", Font.PLAIN, 14);
            this.alignment = 1; // Center by default
            setBackgroundColor(new Color(0, 0, 0, 0)); // Transparent
        }
        
        @Override
        public void render(Graphics2D g) {
            g.setFont(font);
            FontMetrics metrics = g.getFontMetrics();
            int textX;
            
            switch (alignment) {
                case 0: // Left
                    textX = getX();
                    break;
                case 2: // Right
                    textX = getX() + getWidth() - metrics.stringWidth(text);
                    break;
                default: // Center
                    textX = getX() + (getWidth() - metrics.stringWidth(text)) / 2;
            }
            
            int textY = getY() + ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
            g.setColor(getForegroundColor());
            g.drawString(text, textX, textY);
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public void setFont(Font font) {
            this.font = font;
        }
        
        public void setAlignment(int alignment) {
            this.alignment = Math.max(0, Math.min(2, alignment));
        }
    }
    
    /**
     * A panel UI element that can contain other elements.
     */
    public static class Panel extends UIElement {
        private final List<UIElement> children;
        
        public Panel(String id, int x, int y, int width, int height) {
            super(id, x, y, width, height);
            this.children = new ArrayList<>();
        }
        
        @Override
        public void update(double deltaTime) {
            super.update(deltaTime);
            for (UIElement child : children) {
                child.update(deltaTime);
            }
        }
        
        @Override
        public void render(Graphics2D g) {
            // Draw background
            g.setColor(getBackgroundColor());
            g.fillRect(getX(), getY(), getWidth(), getHeight());
            
            // Draw border
            g.setColor(getForegroundColor());
            g.drawRect(getX(), getY(), getWidth(), getHeight());
            
            // Save the original transform
            AffineTransform originalTransform = g.getTransform();
            
            // Translate to panel position
            g.translate(getX(), getY());
            
            // Render children
            for (UIElement child : children) {
                child.render(g);
            }
            
            // Restore the original transform
            g.setTransform(originalTransform);
        }
        
        public void addChild(UIElement child) {
            children.add(child);
        }
        
        public void removeChild(UIElement child) {
            children.remove(child);
        }
        
        public void clearChildren() {
            children.clear();
        }
        
        @Override
        public boolean contains(int mouseX, int mouseY) {
            if (!super.contains(mouseX, mouseY)) {
                return false;
            }
            
            // Check if any child contains the point
            for (UIElement child : children) {
                if (child.contains(mouseX - getX(), mouseY - getY())) {
                    return true;
                }
            }
            
            return true;
        }
    }
} 