package com.cop.handlers;

import com.badlogic.gdx.math.Vector2;
import com.cop.objects.Object_;
import com.dongbat.jbump.*;

public class GravityHandler {
    private final World<Object_> world;
    private final float gravity;
    private final float damping;
    private final float velocityEpsilon; // Threshold velocity after which velocity will be 0

    public GravityHandler(World<Object_> world, float gravity, float damping) {
        this.world = world;
        this.gravity = gravity;
        this.damping = damping;
        this.velocityEpsilon = 0.001f;
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
        float dampingFactor = Math.max(1 - damping, 1 - Math.abs(collision.normal.y * 0.2f));
        // Adjust based on collision angle
        if (collision.normal.y != 0) {
            velocity.y = -velocity.y * dampingFactor;
            if (Math.abs(velocity.y) < velocityEpsilon) {
                velocity.y = 0; // Stop
            }
        }
        if (collision.normal.x != 0) {
            velocity.x = -velocity.x * dampingFactor;
            if (Math.abs(velocity.x) < velocityEpsilon) {
                velocity.x = 0; // Stop
            }
        }
    }
}
