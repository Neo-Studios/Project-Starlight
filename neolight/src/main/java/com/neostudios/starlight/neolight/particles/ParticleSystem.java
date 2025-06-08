package com.neostudios.starlight.neolight.particles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * Manages particle effects in the game.
 */
public class ParticleSystem {
    private static final Logger LOGGER = Logger.getLogger(ParticleSystem.class.getName());
    private static ParticleSystem instance;
    
    private final ConcurrentMap<String, List<Particle>> emitters;
    private final Random random;
    private boolean enabled;
    
    private ParticleSystem() {
        this.emitters = new ConcurrentHashMap<>();
        this.random = new Random();
        this.enabled = true;
    }
    
    public static synchronized ParticleSystem getInstance() {
        if (instance == null) {
            instance = new ParticleSystem();
        }
        return instance;
    }
    
    /**
     * Creates a new particle emitter.
     * @param name Unique identifier for the emitter
     * @param config Configuration for the emitter
     * @return The created emitter
     */
    public ParticleEmitter createEmitter(String name, ParticleEmitterConfig config) {
        List<Particle> particles = new ArrayList<>();
        emitters.put(name, particles);
        return new ParticleEmitter(name, config, particles, random);
    }
    
    /**
     * Updates all particle emitters.
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(double deltaTime) {
        if (!enabled) return;
        
        for (List<Particle> particles : emitters.values()) {
            Iterator<Particle> it = particles.iterator();
            while (it.hasNext()) {
                Particle particle = it.next();
                particle.update(deltaTime);
                if (particle.isDead()) {
                    it.remove();
                }
            }
        }
    }
    
    /**
     * Renders all particles.
     * @param g The graphics context
     */
    public void render(Graphics2D g) {
        if (!enabled) return;
        
        for (List<Particle> particles : emitters.values()) {
            for (Particle particle : particles) {
                particle.render(g);
            }
        }
    }
    
    /**
     * Removes a particle emitter.
     * @param name The name of the emitter to remove
     */
    public void removeEmitter(String name) {
        emitters.remove(name);
        LOGGER.info("Removed particle emitter: " + name);
    }
    
    /**
     * Clears all particle emitters.
     */
    public void clear() {
        emitters.clear();
        LOGGER.info("Cleared all particle emitters");
    }
    
    /**
     * Enables or disables the particle system.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Represents a single particle.
     */
    public static class Particle {
        private float x, y;
        private float vx, vy;
        private float ax, ay;
        private float size;
        private float rotation;
        private float angularVelocity;
        private Color color;
        private float alpha;
        private float life;
        private float maxLife;
        private boolean dead;
        
        public Particle(float x, float y, float vx, float vy, float ax, float ay,
                       float size, float rotation, float angularVelocity,
                       Color color, float alpha, float life) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.ax = ax;
            this.ay = ay;
            this.size = size;
            this.rotation = rotation;
            this.angularVelocity = angularVelocity;
            this.color = color;
            this.alpha = alpha;
            this.life = life;
            this.maxLife = life;
            this.dead = false;
        }
        
        public void update(double deltaTime) {
            // Update position
            vx += ax * deltaTime;
            vy += ay * deltaTime;
            x += vx * deltaTime;
            y += vy * deltaTime;
            
            // Update rotation
            rotation += angularVelocity * deltaTime;
            
            // Update life
            life -= deltaTime;
            if (life <= 0) {
                dead = true;
            }
            
            // Update alpha based on life
            alpha = (life / maxLife) * color.getAlpha() / 255.0f;
        }
        
        public void render(Graphics2D g) {
            // Save the original transform
            java.awt.geom.AffineTransform originalTransform = g.getTransform();
            
            // Set up the particle transform
            g.translate(x, y);
            g.rotate(rotation);
            
            // Set the color with alpha
            g.setColor(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                (int)(alpha * 255)
            ));
            
            // Draw the particle
            g.fillOval(
                (int)(-size/2),
                (int)(-size/2),
                (int)size,
                (int)size
            );
            
            // Restore the original transform
            g.setTransform(originalTransform);
        }
        
        public boolean isDead() {
            return dead;
        }
    }
    
    /**
     * Configuration for a particle emitter.
     */
    public static class ParticleEmitterConfig {
        public float x, y;
        public float minVelocityX, maxVelocityX;
        public float minVelocityY, maxVelocityY;
        public float minAccelerationX, maxAccelerationX;
        public float minAccelerationY, maxAccelerationY;
        public float minSize, maxSize;
        public float minRotation, maxRotation;
        public float minAngularVelocity, maxAngularVelocity;
        public Color startColor, endColor;
        public float minLife, maxLife;
        public int particlesPerSecond;
        public boolean continuous;
        
        public ParticleEmitterConfig() {
            // Default values
            this.minVelocityX = -50;
            this.maxVelocityX = 50;
            this.minVelocityY = -50;
            this.maxVelocityY = 50;
            this.minAccelerationX = 0;
            this.maxAccelerationX = 0;
            this.minAccelerationY = 0;
            this.maxAccelerationY = 0;
            this.minSize = 2;
            this.maxSize = 4;
            this.minRotation = 0;
            this.maxRotation = 360;
            this.minAngularVelocity = -180;
            this.maxAngularVelocity = 180;
            this.startColor = Color.WHITE;
            this.endColor = Color.WHITE;
            this.minLife = 1;
            this.maxLife = 2;
            this.particlesPerSecond = 10;
            this.continuous = true;
        }
    }
    
    /**
     * Represents a particle emitter.
     */
    public static class ParticleEmitter {
        private final String name;
        private final ParticleEmitterConfig config;
        private final List<Particle> particles;
        private final Random random;
        private float emissionAccumulator;
        private boolean active;
        
        public ParticleEmitter(String name, ParticleEmitterConfig config,
                             List<Particle> particles, Random random) {
            this.name = name;
            this.config = config;
            this.particles = particles;
            this.random = random;
            this.emissionAccumulator = 0;
            this.active = true;
        }
        
        public void update(double deltaTime) {
            if (!active) return;
            
            if (config.continuous) {
                emissionAccumulator += deltaTime;
                float emissionInterval = 1.0f / config.particlesPerSecond;
                
                while (emissionAccumulator >= emissionInterval) {
                    emit();
                    emissionAccumulator -= emissionInterval;
                }
            }
        }
        
        public void emit() {
            if (!active) return;
            
            float vx = random.nextFloat() * (config.maxVelocityX - config.minVelocityX) + config.minVelocityX;
            float vy = random.nextFloat() * (config.maxVelocityY - config.minVelocityY) + config.minVelocityY;
            float ax = random.nextFloat() * (config.maxAccelerationX - config.minAccelerationX) + config.minAccelerationX;
            float ay = random.nextFloat() * (config.maxAccelerationY - config.minAccelerationY) + config.minAccelerationY;
            float size = random.nextFloat() * (config.maxSize - config.minSize) + config.minSize;
            float rotation = random.nextFloat() * (config.maxRotation - config.minRotation) + config.minRotation;
            float angularVelocity = random.nextFloat() * (config.maxAngularVelocity - config.minAngularVelocity) + config.minAngularVelocity;
            float life = random.nextFloat() * (config.maxLife - config.minLife) + config.minLife;
            
            particles.add(new Particle(
                config.x, config.y,
                vx, vy,
                ax, ay,
                size,
                rotation,
                angularVelocity,
                config.startColor,
                1.0f,
                life
            ));
        }
        
        public void setPosition(float x, float y) {
            config.x = x;
            config.y = y;
        }
        
        public void setActive(boolean active) {
            this.active = active;
        }
        
        public boolean isActive() {
            return active;
        }
        
        public String getName() {
            return name;
        }
    }
} 