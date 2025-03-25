package com.cop.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

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

    public abstract float getX();
    public abstract float getY();

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
