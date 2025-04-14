package com.cop.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.cop.GameConstants;

public abstract class Object_ {
    public World world;
    protected Body body;

    public Object_(World world) {
        this.world = world;
    }

    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch);

    public abstract void dispose();

    public void update(float delta) {
    }

    public void onResize(int screenWidth, int screenHeight) {
    }

    public float getX() {
        return body.getPosition().x;
    }

    public float getY() {
        return body.getPosition().y;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void setPosition(float x, float y) {
        body.setTransform(x, y, body.getAngle());
    }

    public void setRotation(float angle) {
        body.getTransform().setRotation(angle);
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public World getWorld() {
        return world;
    }
}
