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
import com.cop.objects.*;

import java.util.LinkedList;

import static com.cop.GameConstants.*;

public class ConceptOfPhysicsMain extends ApplicationAdapter {
    public ParametersVisUI parametersVisUI;
    float accumulator;
    ShapeRenderer shapeRenderer;
    SpriteBatch spriteBatch;
    LinkedList<Object_> objects = new LinkedList<>();
    Box2DDebugRenderer debugRenderer;

    @Override
    public void create() {
        GameConstants.main = this;
        world = new World(new Vector2(0, -10f), false);
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth()/GameConstants.GAME_SCALE, Gdx.graphics.getHeight()/GameConstants.GAME_SCALE);
        updateScreenEdges((int) (Gdx.graphics.getWidth()/GameConstants.GAME_SCALE), (int) (Gdx.graphics.getHeight()/GameConstants.GAME_SCALE));
        parametersVisUI = new ParametersVisUI(this);
        parametersVisUI.create();
        debugRenderer = new Box2DDebugRenderer(true,true,true,false,false,true);

        Platform platform = new Platform(world, 1.0f, 0.6f, 300/GameConstants.GAME_SCALE, 200/GameConstants.GAME_SCALE, 10, 5); //Wool
        Wheel wheel = new Wheel(world);
        UniformString uf = new UniformString(world, 150/GameConstants.GAME_SCALE, 250/GameConstants.GAME_SCALE, 100/GameConstants.GAME_SCALE, 5/GameConstants.GAME_SCALE,1,10,1.5e8f).pinToPos(new Vector2(150/GameConstants.GAME_SCALE, 150/GameConstants.GAME_SCALE));
        uf.attachBody(wheel.getBody());
        
        objects.add(platform);
        objects.add(uf);
        objects.add(wheel);
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

        update(Gdx.graphics.getDeltaTime());
    }

    public void update(float delta) {
        accumulator += delta;
        while (accumulator >= GameConstants.STEP) {
            world.step(GameConstants.STEP, GameConstants.VELOCITY_ITERATIONS, GameConstants.POSITION_ITERATIONS);
            accumulator -= GameConstants.STEP;
            world.clearForces();

        }
    }

    @Override
    public void resize(int screenWidth, int screenHeight) {
        ScreenObject_.updateEdges(screenWidth/GameConstants.GAME_SCALE, screenHeight/GameConstants.GAME_SCALE);
        parametersVisUI.resize(screenWidth, screenHeight);
        viewport.setWorldSize(screenWidth / GameConstants.GAME_SCALE, screenHeight / GameConstants.GAME_SCALE);
        viewport.update(screenWidth, screenHeight, true);
        objects.forEach(obj -> obj.onResize(screenWidth, screenHeight));
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
