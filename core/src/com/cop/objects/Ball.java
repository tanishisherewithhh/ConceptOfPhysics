package com.cop.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.cop.GameConstants;

public class Ball extends Object_ {
    public static boolean showVelocityArrow = false;
    /*
    private final Circle circle;
    private final Vector2 velocity = new Vector2();
    private final CollisionFilter collisionFilter = (item, other) -> {
        // Ensure both objects are circular
        if (item.userData instanceof Ball && other.userData instanceof Ball) {
            Ball ball1 = (Ball) item.userData;
            Ball ball2 = (Ball) other.userData;

            // Positions of the two balls
            Vector2 pos1 = new Vector2(ball1.getX() + ball1.circle.radius, ball1.getY());
            Vector2 pos2 = new Vector2(ball2.getX(), ball2.getY());

            // Radii of the balls
            float radius1 = ball1.circle.radius;
            float radius2 = ball2.circle.radius;

            float distance = pos1.dst(pos2);

            if (distance <= radius1 + radius2) {
                return Response.bounce;
            }
            return Response.cross;
        }
        return Response.bounce;
    };

    private final GravityHandler gravityHandler;
     */

    private float radius;
    private Color color = Color.WHITE;

    public Ball(float x, float y, float radius, World world, Vector2 velocity) {
        super(world);
        this.radius = radius;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearVelocity.set(velocity);
        bodyDef.linearDamping = 0;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);

        body.setUserData(this);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);

        // Define the fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 100;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 1f; // Bounciness
        fixtureDef.isSensor = false; // Ensure collisions occur

        body.createFixture(fixtureDef);
        circleShape.dispose();
    }

    public Ball(int x, int y, float radius, World world) {
        this(x, y, radius, world, Vector2.Zero);
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        if(color == null) color = Color.WHITE;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(getX(), getY(), radius);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.circle(getX(),getY(), radius);
        shapeRenderer.end();

        // Render the velocity arrow
        if(showVelocityArrow) renderVelocityArrow(shapeRenderer);
    }

    private void renderVelocityArrow(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Arrow color
        shapeRenderer.setColor(Color.RED);

        // Ball center
        float centerX = getX();
        float centerY = getY();

        // Arrow endpoint (velocity scaled for visibility)
        float arrowScale = 20f;
        float endX = centerX + getVelocity().x * arrowScale;
        float endY = centerY + getVelocity().y * arrowScale;

        // Draw the arrow line
        shapeRenderer.line(centerX, centerY, endX, endY);

        // Draw the arrowhead
        Vector2 direction = new Vector2(getVelocity()).nor(); // Normalize for arrowhead direction
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


    @Override
    public void update(float delta) {
        setCollisionDamping(GameConstants.main.parametersVisUI.getParameterWindow().collisionDamping.getValue());
        setColor(GameConstants.main.parametersVisUI.getParameterWindow().color);
        setRadius(GameConstants.main.parametersVisUI.getParameterWindow().radius.getValue() * 10);
        setAirFriction(GameConstants.main.parametersVisUI.getParameterWindow().airFriction.getValue());

        if(Gdx.input.isTouched()) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            GameConstants.viewport.unproject(touchPos);
            for (Fixture fixture : body.getFixtureList()) {
                if(fixture.testPoint(touchPos)) {
                    body.applyLinearImpulse(Vector2.Zero.setToRandomDirection().scl(body.getGravityScale() * body.getMass() * 2.5f), body.getPosition() ,false);
                }
            }
        }

       // clampToBounds();
    }

    @Override
    public float getX() {
        return body.getPosition().x;
    }

    @Override
    public float getY() {
        return body.getPosition().y;
    }

    public void clampToBounds() {
        float worldWidth = GameConstants.viewport.getWorldWidth();
        float worldHeight = GameConstants.viewport.getWorldHeight();

        body.getPosition().x = Math.max(radius, Math.min(getX(), worldWidth - radius));
        body.getPosition().y = Math.max(radius, Math.min(getY(), worldHeight - radius));
    }

    public void dispose() {}

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
        this.radius = radius;
        for (Fixture fixture : body.getFixtureList().iterator()) {
            CircleShape circleShape = (CircleShape) fixture.getShape();
            circleShape.setRadius(radius);
        }
    }
}
