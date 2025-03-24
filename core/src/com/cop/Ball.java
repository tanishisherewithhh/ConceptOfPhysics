package com.cop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.cop.handlers.GravityHandler;
import com.dongbat.jbump.*;

public class Ball extends Object_{
    private final World<Object_> world;
    private final Item<Object_> item;
    private final Circle circle;
    private float gravity, damping = 0.15f;
    private final Vector2 velocity = new Vector2();
    private final CollisionFilter collisionFilter = (item, other) -> Response.bounce;
    private final GravityHandler gravityHandler;

    public Ball(int x, int y, float radius, float gravity, World<Object_> world, Vector2 velocity) {
        super(world);
        this.circle = new Circle(x, y, radius);
        this.gravity = gravity;
        this.world = world;
        this.gravityHandler = new GravityHandler(world,gravity,damping);
        this.velocity.set(velocity);

        // Create a jBump item for the ball
        this.item = new Item<>(this);
        world.add(this.item, x - radius, y - radius, radius * 2, radius * 2);
    }

    public Ball(int x, int y, float radius, float gravity, World<Object_> world) {
        this(x,y,radius,gravity,world,Vector2.Zero);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(circle.x,circle.y,circle.radius);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(circle.x,circle.y,circle.radius);
        shapeRenderer.end();
    }

    public void syncPosition() {
        float itemX = world.getRect(item).x;
        float itemY = world.getRect(item).y;
        circle.setPosition(itemX + circle.radius, itemY + circle.radius);
    }

    @Override
    public void update(float delta) {
        gravityHandler.applyGravityAndMove(item,velocity,delta,collisionFilter);

        // Sync the circle's position with the item's position
        syncPosition();
    }

    public void dispose(){}

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    public float getGravity() {
        return gravity;
    }

    public float getDamping() {
        return damping;
    }

    public Vector2 getVelocity() {
        return velocity;
    }
}
