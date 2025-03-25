package com.cop.handlers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.cop.objects.Object_;

public class GravityHandler {
   /* private final World world;
    private float gravity;
    private float collisionDamping;
    private final float velocityEpsilon; // Threshold velocity after which velocity will be 0

    public GravityHandler(World<Object_> world, float gravity, float collisionDamping) {
        this.world = world;
        this.gravity = gravity;
        this.collisionDamping = collisionDamping;
        this.velocityEpsilon = MathUtils.FLOAT_ROUNDING_ERROR;
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
        float dampingFactor = Math.max(collisionDamping, 1 - Math.abs(collision.normal.y * 0.2f));

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


    public void setCollisionDamping(float collisionDamping) {
        this.collisionDamping = collisionDamping;
    }
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    */
}
