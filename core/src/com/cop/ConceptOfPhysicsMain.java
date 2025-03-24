package com.cop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cop.objects.Ball;
import com.cop.objects.Object_;
import com.cop.objects.ScreenObject_;
import com.dongbat.jbump.World;

import java.util.LinkedList;

import static com.cop.GameConstants.viewport;
import static com.cop.GameConstants.world;

public class ConceptOfPhysicsMain extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    LinkedList<Ball> objects = new LinkedList<>();
    ParametersVisUI parametersVisUI;

    public static void updateScreenEdges(int screenWidth, int screenHeight) {
        world.update(ScreenObject_.TOP_EDGE.ITEM, 0, screenHeight, screenWidth, 1);
        world.update(ScreenObject_.BOTTOM_EDGE.ITEM, 0, -1, screenWidth, 1);
        world.update(ScreenObject_.LEFT_EDGE.ITEM, -1, 0, 1, screenHeight);
        world.update(ScreenObject_.RIGHT_EDGE.ITEM, screenWidth, 0, 1, screenHeight);
    }

    @Override
    public void create() {
        world = new World<>();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(ScreenUtils.getFrameBufferTexture().getRegionWidth(), ScreenUtils.getFrameBufferTexture().getRegionHeight());
        addScreenEdges(ScreenUtils.getFrameBufferTexture().getRegionWidth(), ScreenUtils.getFrameBufferTexture().getRegionHeight());
        parametersVisUI = new ParametersVisUI(this);
        parametersVisUI.create();
    }

    @Override
    public void render() {
        //ScreenUtils.clear(0f, 0f, 0f, 1f);
        ScreenUtils.clear(0.12f, 0.54f, 0.86f, 1f);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        objects.forEach(obj -> obj.update(Gdx.graphics.getDeltaTime()));

        spriteBatch.begin();
        objects.forEach(obj -> obj.render(shapeRenderer, spriteBatch));

        spriteBatch.end();

        //Screen Collisions:
        float screenWidth = viewport.getScreenWidth();
        float screenHeight = viewport.getScreenHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(0, screenHeight - 1, screenWidth, 1); // Top edge
        shapeRenderer.rect(0, 0, screenWidth, 1); // Bottom edge
        shapeRenderer.rect(0, 0, 1, screenHeight); // Left edge
        shapeRenderer.rect(screenWidth - 1, 0, 1, screenHeight); // Right edge
        shapeRenderer.end();

        parametersVisUI.render();

        objects.forEach(ball -> {
            ball.setGravity(parametersVisUI.parameterWindow.gravity.getValue());
            ball.setDamping(parametersVisUI.parameterWindow.damping.getValue());
            ball.setColor(parametersVisUI.parameterWindow.color);
            ball.setRadius(parametersVisUI.parameterWindow.radius.getValue() * 10);
        });
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight, true);
        viewport.setWorldSize(screenWidth, screenHeight);
        objects.forEach(obj -> obj.onResize(screenWidth, screenHeight));
        updateScreenEdges(screenWidth, screenHeight);

        parametersVisUI.resize(screenWidth, screenHeight);
        //ScreenObject_.refreshEdges();
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
        objects.forEach(Object_::dispose);
        parametersVisUI.dispose();
    }

    private void addScreenEdges(int screenWidth, int screenHeight) {
        world.add(ScreenObject_.TOP_EDGE.ITEM, 0, screenHeight, screenWidth, 1);
        world.add(ScreenObject_.BOTTOM_EDGE.ITEM, 0, -1, screenWidth, 1);
        world.add(ScreenObject_.LEFT_EDGE.ITEM, -1, 0, 1, screenHeight);
        world.add(ScreenObject_.RIGHT_EDGE.ITEM, screenWidth, 0, 1, screenHeight);
    }
}
