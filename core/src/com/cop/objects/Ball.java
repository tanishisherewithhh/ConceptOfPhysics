package com.cop.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.cop.GameConstants;
import com.cop.handlers.GravityHandler;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;

public class Ball extends Object_ {
    public static boolean showVelocityArrow = false;
    private final Circle circle;
    private final Vector2 velocity = new Vector2();
    private final CollisionFilter collisionFilter = (item, other) -> Response.bounce;
    private final GravityHandler gravityHandler;
    private float gravity, damping = 0.15f;
    private Color color = Color.WHITE;

    public Ball(int x, int y, float radius, float gravity, World<Object_> world, Vector2 velocity) {
        super(world);
        this.circle = new Circle(x, y, radius);
        this.gravity = gravity;
        this.world = world;
        this.gravityHandler = new GravityHandler(world, gravity, damping);
        this.velocity.set(velocity);

        // Create a jBump item for the ball
        world.add(this.item, circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2, circle.radius * 2);
    }

    public Ball(int x, int y, float radius, float gravity, World<Object_> world) {
        this(x, y, radius, gravity, world, Vector2.Zero);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if(color == null) color = Color.WHITE;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(circle.x, circle.y, circle.radius);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(circle.x, circle.y, circle.radius);
        shapeRenderer.end();

        // Render the velocity arrow
        if(showVelocityArrow) renderVelocityArrow(shapeRenderer);
    }

    private void renderVelocityArrow(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Arrow color
        shapeRenderer.setColor(Color.RED);

        // Ball center
        float centerX = circle.x;
        float centerY = circle.y;

        // Arrow endpoint (velocity scaled for visibility)
        float arrowScale = 20f;
        float endX = centerX + velocity.x * arrowScale;
        float endY = centerY + velocity.y * arrowScale;

        // Draw the arrow line
        shapeRenderer.line(centerX, centerY, endX, endY);

        // Draw the arrowhead
        Vector2 direction = new Vector2(velocity).nor(); // Normalize for arrowhead direction
        float arrowheadSize = 5f; // Size of the arrowhead
        float arrowLeftX = endX - direction.x * arrowheadSize - direction.y * arrowheadSize;
        float arrowLeftY = endY - direction.y * arrowheadSize + direction.x * arrowheadSize;
        float arrowRightX = endX - direction.x * arrowheadSize + direction.y * arrowheadSize;
        float arrowRightY = endY - direction.y * arrowheadSize - direction.x * arrowheadSize;

        // Draw the arrowhead
        shapeRenderer.line(endX, endY, arrowLeftX, arrowLeftY);
        shapeRenderer.line(endX, endY, arrowRightX, arrowRightY);

        shapeRenderer.end();
    }

    public void syncPosition() {
        float itemX = world.getRect(item).x;
        float itemY = world.getRect(item).y;
        circle.setPosition(itemX + circle.radius, itemY + circle.radius);
    }

    @Override
    public void update(float delta) {
        if(Gdx.input.isTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            GameConstants.viewport.unproject(touchPos);
            if(circle.contains(touchPos)) {
                velocity.add(4, -4);
            }
        }

        gravityHandler.applyGravityAndMove(item, velocity, delta, collisionFilter);

        // Sync the circle's position with the item's position
        syncPosition();
        clampToBounds();
    }

    @Override
    public float getX() {
        return circle.x;
    }

    @Override
    public float getY() {
        return circle.y;
    }

    public void clampToBounds() {
        float worldWidth = GameConstants.viewport.getWorldWidth();
        float worldHeight = GameConstants.viewport.getWorldHeight();

        circle.x = Math.max(circle.radius, Math.min(circle.x, worldWidth - circle.radius));
        circle.y = Math.max(circle.radius, Math.min(circle.y, worldHeight - circle.radius));
    }

    public void dispose() {
    }

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
        gravityHandler.setGravity(gravity);
    }

    public float getDamping() {
        return damping;
    }

    public void setDamping(float damping) {
        this.damping = damping;
        gravityHandler.setDamping(damping);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public void setRadius(float radius) {
        this.circle.radius = radius;
        world.update(item, circle.x - circle.radius, circle.y - circle.radius, circle.radius * 2, circle.radius * 2);
    }
}
