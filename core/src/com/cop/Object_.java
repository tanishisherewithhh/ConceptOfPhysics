package com.cop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.World;

public abstract class Object_ {
    public World<Object_> world;

    public Object_(World<Object_> world){
        this.world = world;
    }

    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch);
    public abstract void dispose();

    public void update(float delta){}

    public void onResize(int screenWidth, int screenHeight){
    }

    public void handleCollisions(Collision collision){}

    public World<Object_> getWorld() {
        return world;
    }
}
