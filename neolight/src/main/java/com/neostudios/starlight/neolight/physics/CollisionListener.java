package com.neostudios.starlight.neolight.physics;

/**
 * Interface for objects that want to be notified of collision events.
 */
public interface CollisionListener {
    /**
     * Called when two bodies start colliding.
     * @param bodyA The first body involved in the collision
     * @param bodyB The second body involved in the collision
     */
    void onCollisionStart(PhysicsBody bodyA, PhysicsBody bodyB);
    
    /**
     * Called when two bodies stop colliding.
     * @param bodyA The first body involved in the collision
     * @param bodyB The second body involved in the collision
     */
    void onCollisionEnd(PhysicsBody bodyA, PhysicsBody bodyB);
} 