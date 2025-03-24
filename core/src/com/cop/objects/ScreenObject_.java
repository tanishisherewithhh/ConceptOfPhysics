package com.cop.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.cop.GameConstants;
import com.dongbat.jbump.Item;

public class ScreenObject_ extends Object_ {
    public static ScreenObject_ TOP_EDGE = new ScreenObject_();
    public static ScreenObject_ BOTTOM_EDGE = new ScreenObject_();
    public static ScreenObject_ LEFT_EDGE = new ScreenObject_();
    public static ScreenObject_ RIGHT_EDGE = new ScreenObject_();

    public Item<Object_> ITEM;

    private ScreenObject_() {
        super(GameConstants.world);
        ITEM = new Item<>(this);
    }

    public static void refreshEdges() {
        TOP_EDGE = new ScreenObject_();
        BOTTOM_EDGE = new ScreenObject_();
        LEFT_EDGE = new ScreenObject_();
        RIGHT_EDGE = new ScreenObject_();
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {}

    @Override
    public void dispose() {}

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }
}
