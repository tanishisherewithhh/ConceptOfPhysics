package com.cop;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameConstants {
    public static final float STEP = 1/120f;
    static final int VELOCITY_ITERATIONS = 8;
    static final int POSITION_ITERATIONS = 3;
    public static World world;
    public static FitViewport viewport;
    public static ConceptOfPhysicsMain main = null;
    public static float GAME_SCALE = 4;
}
