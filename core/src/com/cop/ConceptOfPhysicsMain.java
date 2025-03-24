package com.cop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.World;

public class ConceptOfPhysicsMain extends ApplicationAdapter {
	static World<Object_> world;
	FitViewport viewport;
	ShapeRenderer shapeRenderer;
	SpriteBatch spriteBatch;
	Ball ball;

	@Override
	public void create() {
		world = new World<>();
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		viewport = new FitViewport(ScreenUtils.getFrameBufferTexture().getRegionWidth(), ScreenUtils.getFrameBufferTexture().getRegionHeight());
		ball = new Ball(250,250,20f, 0.10f, world, new Vector2(2f, -0.10f));
		addScreenEdges(world, ScreenUtils.getFrameBufferTexture().getRegionWidth(), ScreenUtils.getFrameBufferTexture().getRegionHeight());
	}

	@Override
	public void render() {
		ScreenUtils.clear(0.12f,0.54f,0.86f,1f);
		viewport.apply();
		ball.update(Gdx.graphics.getDeltaTime());
		spriteBatch.setProjectionMatrix(viewport.getCamera().combined);

		spriteBatch.begin();
		ball.render(shapeRenderer,spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height,true);
		ball.onResize(width,height);
		//ScreenObject_.refreshEdges();
		addScreenEdges(world, width,height);
	}

	@Override
	public void resume() {

	}

	public static void addScreenEdges(World<Object_> world, int screenWidth, int screenHeight) {
		world.add(new Item<>(ScreenObject_.TOP_EDGE), 0, screenHeight, screenWidth, 1);
		world.add(new Item<>(ScreenObject_.BOTTOM_EDGE), 0, -1, screenWidth, 1);
		world.add(new Item<>(ScreenObject_.LEFT_EDGE), -1, 0, 1, screenHeight);
		world.add(new Item<>(ScreenObject_.RIGHT_EDGE), screenWidth, 0, 1, screenHeight);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		shapeRenderer.dispose();
	}
}
