package com.cop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ScreenObject_ extends Object_{
    public static ScreenObject_ TOP_EDGE = new ScreenObject_();
    public static ScreenObject_ BOTTOM_EDGE = new ScreenObject_();
    public static ScreenObject_ LEFT_EDGE = new ScreenObject_();
    public static ScreenObject_ RIGHT_EDGE = new ScreenObject_();

    private ScreenObject_() {
        super(ConceptOfPhysicsMain.world);
    }

    public static void refreshEdges(){
        ScreenObject_ TOP_EDGE = new ScreenObject_();
        ScreenObject_ BOTTOM_EDGE = new ScreenObject_();
        ScreenObject_ LEFT_EDGE = new ScreenObject_();
        ScreenObject_ RIGHT_EDGE = new ScreenObject_();
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {}

    @Override
    public void dispose() {}
}
