package com.cop.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.dongbat.jbump.Collision;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public abstract class Object_ {
    public World<Object_> world;
    public Item<Object_> item;

    public Object_(World<Object_> world) {
        this.world = world;
        this.item = new Item<>(this);
    }

    public abstract void render(ShapeRenderer shapeRenderer, SpriteBatch batch);

    public abstract void dispose();

    public void update(float delta) {
    }

    public void onResize(int screenWidth, int screenHeight) {
    }

    public void handleCollisions(Collision collision) {
    }

    public abstract float getX();
    public abstract float getY();

    public World<Object_> getWorld() {
        return world;
    }
}
