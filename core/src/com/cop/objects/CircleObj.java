package com.cop.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.cop.GameConstants;
import com.cop.ParametersVisUI;

public class CircleObj extends Object_ {
    public static boolean showVelocityArrow = false;
    protected float radius;
    protected Color color = Color.WHITE;

    public CircleObj(float x, float y, float radius, World world, Vector2 velocity) {
        super(world);
        this.radius = radius;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearVelocity.set(velocity);
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        // Define the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 3;
        fixtureDef.restitution = 0.1f; // Bounciness
        fixtureDef.isSensor = false; // Ensure collisions occur

        body.createFixture(fixtureDef);
        circleShape.dispose();
    }

    public CircleObj(int x, int y, float radius, World world) {
        this(x, y, radius, world, Vector2.Zero);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if (color == null) color = Color.WHITE;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(getX(), getY(), radius,Math.max(1, (int)(6 * (float)Math.sqrt(radius))));
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(getX(), getY(), radius,Math.max(1, (int)(6 * (float)Math.sqrt(radius))));
        shapeRenderer.end();

        // Render the velocity arrow
        if (showVelocityArrow) renderVelocityArrow(shapeRenderer);
    }

    private void renderVelocityArrow(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Arrow color
        shapeRenderer.setColor(Color.RED);

        // CircleObj center
        float centerX = getX();
        float centerY = getY();

        // Arrow endpoint (velocity scaled for visibility)
        float arrowScale = 20f / GameConstants.GAME_SCALE;
        float endX = centerX + getVelocity().x * arrowScale;
        float endY = centerY + getVelocity().y * arrowScale;

        shapeRenderer.line(centerX, centerY, endX, endY);

        Vector2 direction = new Vector2(getVelocity()).nor(); // Normalize for arrowhead direction
        float arrowheadSize = 5f / GameConstants.GAME_SCALE;
        float arrowLeftX = endX - direction.x * arrowheadSize - direction.y * arrowheadSize;
        float arrowLeftY = endY - direction.y * arrowheadSize + direction.x * arrowheadSize;
        float arrowRightX = endX - direction.x * arrowheadSize + direction.y * arrowheadSize;
        float arrowRightY = endY - direction.y * arrowheadSize - direction.x * arrowheadSize;

        shapeRenderer.line(endX, endY, arrowLeftX, arrowLeftY);
        shapeRenderer.line(endX, endY, arrowRightX, arrowRightY);

        shapeRenderer.end();
    }


    @Override
    public void update(float delta) {
        setCollisionDamping(ParametersVisUI.getCollisionDamping());
        setColor(ParametersVisUI.getColor());
        setRadius(ParametersVisUI.getRadius() * 10);
        setAirFriction(ParametersVisUI.getAirFriction());

        if (Gdx.input.isTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            GameConstants.viewport.unproject(touchPos);
            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.testPoint(touchPos)) {
                    body.applyLinearImpulse(Vector2.Zero.setToRandomDirection().scl(body.getGravityScale() * body.getMass() * 2.5f), body.getPosition(), false);
                }
            }
        }

        clampToBounds();
    }

    public void clampToBounds() {
        float worldWidth = GameConstants.viewport.getWorldWidth();
        float worldHeight = GameConstants.viewport.getWorldHeight();

        body.getPosition().x = Math.max(radius, Math.min(getX(), worldWidth - radius));
        body.getPosition().y = Math.max(radius, Math.min(getY(), worldHeight - radius));
    }

    public void dispose() {
    }

    public float getCollisionDamping() {
        return body.getAngularDamping();
    }

    public void setCollisionDamping(float collisionDamping) {
        for (Fixture fixture : body.getFixtureList()) {
            fixture.setRestitution(collisionDamping); // Updates the restitution (bounciness)
        }
    }

    public void setAirFriction(float airFriction) {
        body.setLinearDamping(airFriction);
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setRadius(float radius) {
        this.radius = radius/GameConstants.GAME_SCALE;
        for (Fixture fixture : body.getFixtureList().iterator()) {
            CircleShape circleShape = (CircleShape) fixture.getShape();
            circleShape.setRadius(this.radius);
        }
    }
}
