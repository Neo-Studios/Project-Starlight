package com.neostudios.starlight.neolight.physics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Manages physics simulation including collision detection and basic physics.
 */
public class PhysicsSystem {
    private static final Logger LOGGER = Logger.getLogger(PhysicsSystem.class.getName());
    private static PhysicsSystem instance;
    
    private final Map<Integer, PhysicsBody> bodies;
    private final List<CollisionListener> collisionListeners;
    private final Set<CollisionPair> lastFrameCollisions;
    private float gravity;
    private float friction;
    private boolean enabled;
    
    private PhysicsSystem() {
        this.bodies = new ConcurrentHashMap<>();
        this.collisionListeners = new ArrayList<>();
        this.lastFrameCollisions = new HashSet<>();
        this.gravity = 9.8f;
        this.friction = 0.1f;
        this.enabled = true;
    }
    
    public static synchronized PhysicsSystem getInstance() {
        if (instance == null) {
            instance = new PhysicsSystem();
        }
        return instance;
    }
    
    /**
     * Updates all physics bodies and checks for collisions.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        // Update positions and velocities
        for (PhysicsBody body : bodies.values()) {
            if (!body.isStatic()) {
                // Apply gravity
                if (body.isAffectedByGravity()) {
                    body.velocity.y += gravity * deltaTime;
                }
                
                // Apply friction
                if (body.isOnGround()) {
                    body.velocity.x *= (1.0f - friction);
                }
                
                // Update position
                body.position.x += body.velocity.x * deltaTime;
                body.position.y += body.velocity.y * deltaTime;
                
                // Update rotation if needed
                if (body.angularVelocity != 0) {
                    body.rotation += body.angularVelocity * deltaTime;
                }
            }
        }
        
        // Check for collisions
        Set<CollisionPair> currentCollisions = new HashSet<>();
        List<PhysicsBody> bodyList = new ArrayList<>(bodies.values());
        
        for (int i = 0; i < bodyList.size(); i++) {
            PhysicsBody bodyA = bodyList.get(i);
            if (!bodyA.isActive()) continue;
            
            for (int j = i + 1; j < bodyList.size(); j++) {
                PhysicsBody bodyB = bodyList.get(j);
                if (!bodyB.isActive()) continue;
                
                // Skip if both bodies are static
                if (bodyA.isStatic() && bodyB.isStatic()) continue;
                
                // Skip if collision is ignored
                if (bodyA.ignoresCollisionWith(bodyB) || bodyB.ignoresCollisionWith(bodyA)) continue;
                
                CollisionPair pair = new CollisionPair(bodyA, bodyB);
                if (checkCollision(bodyA, bodyB)) {
                    currentCollisions.add(pair);
                    
                    // Handle collision response
                    resolveCollision(bodyA, bodyB);
                    
                    // Notify listeners of new collision
                    if (!lastFrameCollisions.contains(pair)) {
                        for (CollisionListener listener : collisionListeners) {
                            listener.onCollisionStart(bodyA, bodyB);
                        }
                    }
                }
            }
        }
        
        // Notify listeners of ended collisions
        for (CollisionPair pair : lastFrameCollisions) {
            if (!currentCollisions.contains(pair)) {
                for (CollisionListener listener : collisionListeners) {
                    listener.onCollisionEnd(pair.bodyA, pair.bodyB);
                }
            }
        }
        
        // Update last frame collisions
        lastFrameCollisions.clear();
        lastFrameCollisions.addAll(currentCollisions);
    }
    
    /**
     * Checks for collision between two physics bodies.
     */
    private boolean checkCollision(PhysicsBody a, PhysicsBody b) {
        // Simple AABB collision detection
        return a.getBounds().intersects(b.getBounds());
    }
    
    /**
     * Resolves collision between two physics bodies.
     */
    private void resolveCollision(PhysicsBody a, PhysicsBody b) {
        // Calculate collision normal and penetration depth
        float dx = b.position.x - a.position.x;
        float dy = b.position.y - a.position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        
        if (distance == 0) return;
        
        float nx = dx / distance;
        float ny = dy / distance;
        
        // Calculate relative velocity
        float relativeVelocityX = b.velocity.x - a.velocity.x;
        float relativeVelocityY = b.velocity.y - a.velocity.y;
        
        // Calculate relative velocity in terms of the normal direction
        float velocityAlongNormal = relativeVelocityX * nx + relativeVelocityY * ny;
        
        // Do not resolve if objects are moving apart
        if (velocityAlongNormal > 0) return;
        
        // Calculate restitution (bounciness)
        float restitution = Math.min(a.getRestitution(), b.getRestitution());
        
        // Calculate impulse scalar
        float impulseScalar = -(1 + restitution) * velocityAlongNormal;
        impulseScalar /= a.getInverseMass() + b.getInverseMass();
        
        // Apply impulse
        float impulseX = impulseScalar * nx;
        float impulseY = impulseScalar * ny;
        
        if (!a.isStatic()) {
            a.velocity.x -= impulseX * a.getInverseMass();
            a.velocity.y -= impulseY * a.getInverseMass();
        }
        
        if (!b.isStatic()) {
            b.velocity.x += impulseX * b.getInverseMass();
            b.velocity.y += impulseY * b.getInverseMass();
        }
        
        // Positional correction to prevent sinking
        float percent = 0.2f; // penetration recovery rate
        float slop = 0.01f; // penetration allowance
        float correction = Math.max(a.getBounds().getHeight() + b.getBounds().getHeight() - distance - slop, 0.0f) 
                         / (a.getInverseMass() + b.getInverseMass()) * percent;
        
        float correctionX = nx * correction;
        float correctionY = ny * correction;
        
        if (!a.isStatic()) {
            a.position.x -= correctionX * a.getInverseMass();
            a.position.y -= correctionY * a.getInverseMass();
        }
        
        if (!b.isStatic()) {
            b.position.x += correctionX * b.getInverseMass();
            b.position.y += correctionY * b.getInverseMass();
        }
    }
    
    /**
     * Adds a physics body to the system.
     */
    public void addBody(PhysicsBody body) {
        bodies.put(body.getId(), body);
        LOGGER.info("Added physics body: " + body.getId());
    }
    
    /**
     * Removes a physics body from the system.
     */
    public void removeBody(PhysicsBody body) {
        bodies.remove(body.getId());
        LOGGER.info("Removed physics body: " + body.getId());
    }
    
    /**
     * Adds a collision listener.
     */
    public void addCollisionListener(CollisionListener listener) {
        collisionListeners.add(listener);
    }
    
    /**
     * Removes a collision listener.
     */
    public void removeCollisionListener(CollisionListener listener) {
        collisionListeners.remove(listener);
    }
    
    /**
     * Sets the gravity strength.
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    /**
     * Sets the friction coefficient.
     */
    public void setFriction(float friction) {
        this.friction = Math.max(0, Math.min(1, friction));
    }
    
    /**
     * Enables or disables the physics system.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Clears all physics bodies and listeners.
     */
    public void clear() {
        bodies.clear();
        collisionListeners.clear();
        lastFrameCollisions.clear();
        LOGGER.info("Cleared physics system");
    }
    
    /**
     * Represents a pair of colliding bodies.
     */
    private static class CollisionPair {
        final PhysicsBody bodyA;
        final PhysicsBody bodyB;
        
        CollisionPair(PhysicsBody a, PhysicsBody b) {
            this.bodyA = a;
            this.bodyB = b;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CollisionPair that = (CollisionPair) o;
            return (bodyA == that.bodyA && bodyB == that.bodyB) ||
                   (bodyA == that.bodyB && bodyB == that.bodyA);
        }
        
        @Override
        public int hashCode() {
            return bodyA.hashCode() + bodyB.hashCode();
        }
    }
} 