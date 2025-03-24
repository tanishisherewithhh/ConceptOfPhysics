package com.cop.handlers;

import com.badlogic.gdx.math.Vector2;
import com.cop.objects.Object_;
import com.dongbat.jbump.*;

public class GravityHandler {
    private final World<Object_> world;
    private float gravity;
    private float damping;
    private final float velocityEpsilon; // Threshold velocity after which velocity will be 0

    public GravityHandler(World<Object_> world, float gravity, float damping) {
        this.world = world;
        this.gravity = gravity;
        this.damping = damping;
        this.velocityEpsilon = 0.01f;
    }

    public void applyGravityAndMove(Item<Object_> item, Vector2 velocity, float delta, CollisionFilter collisionFilter) {
        // Apply gravity
        velocity.y -= gravity;

        // Predict future position
        float futureX = world.getRect(item).x + velocity.x;
        float futureY = world.getRect(item).y + velocity.y;

        // Move and handle collisions
        Response.Result result = world.move(item, futureX, futureY, collisionFilter);

        for (int i = 0; i < result.projectedCollisions.size(); i++) {
            Collision collision = result.projectedCollisions.get(i);
            handleCollisions(collision, velocity);
        }
    }

    private void handleCollisions(Collision collision, Vector2 velocity) {
        float dampingFactor = Math.max(damping, 1 - Math.abs(collision.normal.y * 0.2f));

        // Get the collision normal
        Vector2 normal = new Vector2(collision.normal.x, collision.normal.y);

        // Calculate the reflection vector
        Vector2 incomingVelocity = new Vector2(velocity);
        Vector2 reflection = incomingVelocity.sub(normal.scl(2 * incomingVelocity.dot(normal))).scl(dampingFactor);

        // Update the velocity based on the reflection
        velocity.set(reflection);

        // Stop velocity if it's below a threshold (to prevent jittering)
        if (velocity.len() < velocityEpsilon) {
            velocity.setZero();
        }
    }


    public void setDamping(float damping) {
        this.damping = damping;
    }
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
}
