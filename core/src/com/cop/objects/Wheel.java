package com.cop.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.cop.GameConstants;

//Circle but rotating
public class Wheel extends Ball {
    protected Body axleBody;
    protected RevoluteJoint wheelJoint;

    public Wheel(World world) {
        super(GameConstants.viewport.getWorldWidth() / 2, GameConstants.viewport.getWorldHeight() / 2, 20f, world, Vector2.Zero);
        /* Axle */
        PolygonShape axleShape = new PolygonShape();
        axleShape.setAsBox(5,5);
        // fixture
        FixtureDef axleFixture = new FixtureDef();
        axleFixture.shape = axleShape;
        axleFixture.density = 5f;
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
        axleBody.setGravityScale(0);

        RevoluteJointDef wheelRevoluteJointDef = new RevoluteJointDef();
        wheelRevoluteJointDef.initialize(body, axleBody, body.getWorldCenter());
        wheelRevoluteJointDef.enableMotor = true;
        wheelRevoluteJointDef.maxMotorTorque = 1000000;
        wheelJoint = (RevoluteJoint) world.createJoint(wheelRevoluteJointDef);


        wheelJoint.setUserData(this);
        body.setUserData(this);
        axleBody.setUserData(this);


        axleShape.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        //10 rotations/s
        wheelJoint.setMotorSpeed((float) -(2 * Math.PI * 1));
    }
}
