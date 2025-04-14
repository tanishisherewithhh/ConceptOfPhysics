package com.cop.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.cop.GameConstants;

public class Platform extends Object_ {
    float width, height;
    private boolean isDragging = false;
    private final Vector2 dragOffset = new Vector2(); // Offset to track relative mouse position

    public Platform(World world, float restitution, float friction, float x, float y, float width, float height) {
        super(world);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2); // Box is centered at (0, 0) with half dimensions

        // Define the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = friction; // Friction for ball interactions
        fixtureDef.restitution = restitution;

        body.createFixture(fixtureDef);

        shape.dispose();

        this.width = width;
        this.height = height;

        InputMultiplexer inputMultiplexer = getInputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }


    public void setSize(float width, float height) {
        float friction = 0.0f;
        float restitution = 0.0f;
        // Remove the old fixture
        for (Fixture fixture : body.getFixtureList()) {
            friction = Math.max(fixture.getFriction(), friction);
            restitution = Math.max(fixture.getRestitution(), restitution);
            body.destroyFixture(fixture);
        }

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        this.width = width;
        this.height = height;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.CHARTREUSE);
        shapeRenderer.rect(
                getX() - width / 2, // Adjust to draw from the center
                getY() - height / 2,
                width,
                height
        );
        shapeRenderer.end();
    }

    public boolean isMouseOver(Vector2 mousePosition, float platformX, float platformY, float platformWidth, float platformHeight) {
        return mousePosition.x >= platformX - platformWidth / 2 &&
                mousePosition.x <= platformX + platformWidth / 2 &&
                mousePosition.y >= platformY - platformHeight / 2 &&
                mousePosition.y <= platformY + platformHeight / 2;
    }

    @Override
    public void update(float delta) {
        Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        GameConstants.viewport.unproject(mousePosition);

        if (Gdx.input.justTouched()) {
            // Check if mouse is over the platform
            if (isMouseOver(mousePosition, getPosition().x, getPosition().y, width, height)) {
                isDragging = true;

                // Calculate offset between platform center and mouse
                dragOffset.set(getPosition().x - mousePosition.x, getPosition().y - mousePosition.y);
            }
        } else if (!Gdx.input.isTouched()) {
            // Stop dragging when the mouse is released
            isDragging = false;
        }

        if (isDragging) {
            // While dragging, update the platform's position
            setPosition(mousePosition.x + dragOffset.x, mousePosition.y + dragOffset.y);
        }
    }

    private InputMultiplexer getInputMultiplexer() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer(Gdx.input.getInputProcessor());
        inputMultiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float v, float v1) {
                Vector2 mousePosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                GameConstants.viewport.unproject(mousePosition);

                if (isMouseOver(mousePosition, getPosition().x, getPosition().y, width, height)) {
                    setSize(width - v1 * 2, height);
                    return true;
                }
                return false;
            }
        });
        return inputMultiplexer;
    }

    @Override
    public void dispose() {

    }
}
