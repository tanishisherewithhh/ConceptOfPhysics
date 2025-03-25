package com.cop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cop.objects.Object_;
import com.cop.objects.ScreenObject_;

import java.util.LinkedList;

import static com.cop.GameConstants.viewport;
import static com.cop.GameConstants.world;

public class ConceptOfPhysicsMain extends ApplicationAdapter {
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    LinkedList<Object_> objects = new LinkedList<>();
    public ParametersVisUI parametersVisUI;
    Box2DDebugRenderer debugRenderer;

    @Override
    public void create() {
        GameConstants.main = this;
        world = new World(new Vector2(0,-10f),false);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(ScreenUtils.getFrameBufferTexture().getRegionWidth(), ScreenUtils.getFrameBufferTexture().getRegionHeight());
        updateScreenEdges(ScreenUtils.getFrameBufferTexture().getRegionWidth(), ScreenUtils.getFrameBufferTexture().getRegionHeight());

        parametersVisUI = new ParametersVisUI(this);
        parametersVisUI.create();
        debugRenderer = new Box2DDebugRenderer();
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

        parametersVisUI.render();

        debugRenderer.render(world, viewport.getCamera().combined);
        world.step(1/30f,120,60);
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight, true);
        viewport.setWorldSize(screenWidth, screenHeight);
        objects.forEach(obj -> obj.onResize(screenWidth, screenHeight));
        ScreenObject_.updateEdges(screenWidth, screenHeight);

        parametersVisUI.resize(screenWidth, screenHeight);
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
        world.dispose();
    }

    private void updateScreenEdges(int screenWidth, int screenHeight) {
        ScreenObject_.initEdges(screenWidth, screenHeight);
    }
}
