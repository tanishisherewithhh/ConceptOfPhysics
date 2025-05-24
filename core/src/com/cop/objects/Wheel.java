package com.cop.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.cop.GameConstants;

//Circle but rotating
public class Wheel extends CircleObj {
    protected Body axleBody;
    protected RevoluteJoint wheelJoint;

    public Wheel(float x, float y, float radius, World world, Vector2 velocity) {
        super(x, y, radius, world, velocity);
        /* Axle */
        PolygonShape axleShape = new PolygonShape();
        axleShape.setAsBox(5 / GameConstants.GAME_SCALE, 5 / GameConstants.GAME_SCALE);
        // fixture
        FixtureDef axleFixture = new FixtureDef();
        axleFixture.shape = axleShape;
        axleFixture.density = 1;
        axleFixture.friction = 0.5f;
        axleFixture.restitution = 0.3f;
        axleFixture.filter.groupIndex = -1;
        // body definition
        BodyDef axleBodyDef = new BodyDef();
        axleBodyDef.type = BodyDef.BodyType.DynamicBody;
        axleBodyDef.position.set(body.getPosition());

        axleBody = world.createBody(axleBodyDef);
        axleBody.createFixture(axleFixture);
        axleBody.setAwake(true);
        axleBody.setActive(true);
        axleBody.setFixedRotation(true);
        //axleBody.setGravityScale(0);

        RevoluteJointDef wheelRevoluteJointDef = new RevoluteJointDef();
        wheelRevoluteJointDef.initialize(body, axleBody, body.getWorldCenter());
        wheelRevoluteJointDef.enableMotor = true;
        wheelRevoluteJointDef.maxMotorTorque = 10000;
        wheelJoint = (RevoluteJoint) world.createJoint(wheelRevoluteJointDef);


        wheelJoint.setUserData(this);
        body.setUserData(this);
        axleBody.setUserData(this);


        axleShape.dispose();

        //100 rotations/s
        setMotorSpeedRotPerS(100);
    }

    public Wheel(World world) {
        this(GameConstants.viewport.getWorldWidth() / 2, GameConstants.viewport.getWorldHeight() / 2, 0.5f, world, Vector2.Zero);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        lock();

    }

    public void setMotorSpeed(float motorSpeed) {
        wheelJoint.setMotorSpeed(motorSpeed);
    }

    public void setMotorSpeedRotPerS(float rotPerSecond) {
        wheelJoint.setMotorSpeed((float) (2 * Math.PI * rotPerSecond));
    }

    public void setMotorTorque(int torque) {
        wheelJoint.setMaxMotorTorque(torque);
    }

    /*
     function for hopefully locking the wheel
     */
    public void lock() {
        axleBody.setActive(false);
    }
}
