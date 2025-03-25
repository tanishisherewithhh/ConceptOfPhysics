package com.cop.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.cop.GameConstants;

public class ScreenObject_ extends Object_ {
    public static ScreenObject_ TOP_EDGE;
    public static ScreenObject_ BOTTOM_EDGE;
    public static ScreenObject_ LEFT_EDGE;
    public static ScreenObject_ RIGHT_EDGE;

    private ScreenObject_(float x1, float y1, float width, float height) {
        super(GameConstants.world);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody; // Edges are static
        body = world.createBody(bodyDef);
        body.setUserData(this);

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(x1, y1, x1 + width, y1 + height);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = edgeShape;
        fixtureDef.density = 0f; // Static edges don't require density
        fixtureDef.friction = 0f; // Friction for collisions
        fixtureDef.restitution = 1f; // Bounciness
        body.getPosition().set(new Vector2(x1, y1));

        body.createFixture(fixtureDef);

        edgeShape.dispose();
    }

    public static void initEdges(float screenWidth, float screenHeight) {
        TOP_EDGE = new ScreenObject_(0, screenHeight, screenWidth, 1);
        BOTTOM_EDGE = new ScreenObject_(0, -1, screenWidth, 1);
        LEFT_EDGE = new ScreenObject_(-1, 0, 1, screenHeight);
        RIGHT_EDGE = new ScreenObject_(screenWidth, 0, 1, screenHeight);
    }

    public static void updateEdges(float screenWidth, float screenHeight) {
        TOP_EDGE.updateEdge(0, screenHeight, screenWidth, 1);
        BOTTOM_EDGE.updateEdge(0, -1, screenWidth, 1);
        LEFT_EDGE.updateEdge(-1, 0, 1, screenHeight);
        RIGHT_EDGE.updateEdge(screenWidth, 0, 1, screenHeight);
    }

    public void updateEdge(float x1, float y1, float width, float height) {
        // Remove existing fixtures
        body.destroyFixture(body.getFixtureList().first());

        // Create a new EdgeShape with updated dimensions
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(x1, y1, x1 + width, y1 + height); // Update edge endpoints

        // Define new Fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = edgeShape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 1f;

        body.createFixture(fixtureDef);

        edgeShape.dispose();
    }


    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Render the edge visually as a line
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        // Retrieve the vertices of the edge
        EdgeShape shape = (EdgeShape) body.getFixtureList().first().getShape();
        Vector2 vertex0 = new Vector2();
        Vector2 vertex1 = new Vector2();
        shape.getVertex0(vertex0); // Start point of the edge
        shape.getVertex1(vertex1); // End point of the edge

        // Draw the line between the two vertices
        shapeRenderer.line(vertex0.x, vertex0.y, vertex1.x, vertex1.y);

        shapeRenderer.end();
    }

    @Override
    public void dispose() {
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }
}
