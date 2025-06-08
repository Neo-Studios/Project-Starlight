package com.neostudios.starlight.neolight.physics;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a physical object in the game world.
 */
public class PhysicsBody {
    private static int nextId = 0;
    
    private final int id;
    private final Vector2 position;
    private final Vector2 velocity;
    private float rotation;
    private float angularVelocity;
    private float mass;
    private float restitution;
    private boolean staticBody;
    private boolean affectedByGravity;
    private boolean active;
    private boolean onGround;
    private final Set<Integer> ignoredCollisions;
    private Rectangle bounds;
    
    public PhysicsBody(float x, float y, float width, float height) {
        this.id = nextId++;
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.rotation = 0;
        this.angularVelocity = 0;
        this.mass = 1.0f;
        this.restitution = 0.2f;
        this.staticBody = false;
        this.affectedByGravity = true;
        this.active = true;
        this.onGround = false;
        this.ignoredCollisions = new HashSet<>();
        this.bounds = new Rectangle((int)x, (int)y, (int)width, (int)height);
    }
    
    public int getId() {
        return id;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
        updateBounds();
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public void setVelocity(float x, float y) {
        velocity.x = x;
        velocity.y = y;
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    public float getAngularVelocity() {
        return angularVelocity;
    }
    
    public void setAngularVelocity(float angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    public float getMass() {
        return mass;
    }
    
    public void setMass(float mass) {
        this.mass = mass > 0 ? mass : Float.POSITIVE_INFINITY;
    }
    
    public float getInverseMass() {
        return mass > 0 ? 1.0f / mass : 0;
    }
    
    public float getRestitution() {
        return restitution;
    }
    
    public void setRestitution(float restitution) {
        this.restitution = Math.max(0, Math.min(1, restitution));
    }
    
    public boolean isStatic() {
        return staticBody;
    }
    
    public void setStatic(boolean staticBody) {
        this.staticBody = staticBody;
        if (staticBody) {
            mass = Float.POSITIVE_INFINITY;
            velocity.x = 0;
            velocity.y = 0;
            angularVelocity = 0;
        }
    }
    
    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }
    
    public void setAffectedByGravity(boolean affectedByGravity) {
        this.affectedByGravity = affectedByGravity;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isOnGround() {
        return onGround;
    }
    
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
    
    public Rectangle getBounds() {
        return bounds;
    }
    
    public void setSize(float width, float height) {
        bounds.width = (int)width;
        bounds.height = (int)height;
        updateBounds();
    }
    
    public void ignoreCollisionWith(PhysicsBody other) {
        ignoredCollisions.add(other.getId());
    }
    
    public void unignoreCollisionWith(PhysicsBody other) {
        ignoredCollisions.remove(other.getId());
    }
    
    public boolean ignoresCollisionWith(PhysicsBody other) {
        return ignoredCollisions.contains(other.getId());
    }
    
    private void updateBounds() {
        bounds.x = (int)position.x;
        bounds.y = (int)position.y;
    }
    
    /**
     * Simple 2D vector class for physics calculations.
     */
    public static class Vector2 {
        public float x;
        public float y;
        
        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }
        
        public void add(Vector2 other) {
            x += other.x;
            y += other.y;
        }
        
        public void subtract(Vector2 other) {
            x -= other.x;
            y -= other.y;
        }
        
        public void multiply(float scalar) {
            x *= scalar;
            y *= scalar;
        }
        
        public float length() {
            return (float) Math.sqrt(x * x + y * y);
        }
        
        public void normalize() {
            float len = length();
            if (len > 0) {
                x /= len;
                y /= len;
            }
        }
        
        public float dot(Vector2 other) {
            return x * other.x + y * other.y;
        }
    }
} 