package com.cop.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJoint;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.cop.GameConstants;

public class UniformString extends Object_ {
    private boolean isPinning = false;
    private final Body[] stringBodies;
    private final DistanceJoint[] stringJoints;
    private final float width;
    private final float height;
    private final float massPerLength;
    private final float breakingStress; //In newtons normally
    private final float crossSectionalArea = 0.0001f; // 1 cm² cross-section
    private final boolean highlightStress = true; // Toggle this as needed
    private Body attachedBody = null;
    private RopeJoint attachedBodyJoint = null;
    private Vector2 pinTargetStart;
    private Vector2 pinTargetEnd;

    public UniformString(World world, float x, float y, float width, float height, float massPerLength, int segmentCount, float breakingStress) {
        super(world);
        this.width = width;
        this.height = height;
        this.breakingStress = breakingStress;
        this.massPerLength = massPerLength;
        this.stringBodies = new Body[segmentCount + 1];
        this.stringJoints = new DistanceJoint[segmentCount];

        float segmentWidth = width / segmentCount;

        // Create individual bodies
        for (int i = 0; i <= segmentCount; i++) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(x + i * segmentWidth, y);

            Body body = world.createBody(bodyDef);

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(segmentWidth / 2, height / 2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = massPerLength / segmentCount; // Assign density based on mass per length
            fixtureDef.friction = 0.01f;
            fixtureDef.restitution = 0.8f;

            body.createFixture(fixtureDef);
            body.setFixedRotation(true);
            shape.dispose();

            body.setUserData(this);
            stringBodies[i] = body;
        }

        body = stringBodies[0];

        // Connect bodies using distance joints
        for (int i = 1; i <= segmentCount; i++) {
            DistanceJointDef jointDef = new DistanceJointDef();
            jointDef.bodyA = stringBodies[i - 1];
            jointDef.bodyB = stringBodies[i];
            jointDef.length = segmentWidth; // Maintain uniform spacing
            jointDef.collideConnected = false;
            jointDef.frequencyHz = 10;
            jointDef.dampingRatio = 0.7f;

            stringJoints[i - 1] = (DistanceJoint) world.createJoint(jointDef);
        }
    }

    public UniformString pinToPos() {
        pinString(body.getPosition());
        return this;
    }

    public UniformString pinToPos(Vector2 pinPos2) {
        pinString(body.getPosition(), pinPos2);
        return this;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < stringBodies.length - 1; i++) {
            Vector2 pos1 = stringBodies[i].getPosition();
            if (stringJoints[i] == null) {
                shapeRenderer.rect(pos1.x, pos1.y, 1.0f, 1.0f);
                continue;
            }
            Vector2 pos2 = stringBodies[i + 1].getPosition();

            // Default color (brown) if stress highlighting is off
            Color color1 = new Color(0.6f, 0.3f, 0.1f, 1f);
            Color color2 = color1;

            if (highlightStress) {
                // Get the current reaction force at the joint
                Vector2 reactionForce = stringJoints[i].getReactionForce(1 / GameConstants.STEP);
                float forceMagnitude = reactionForce.len();
                float stress = forceMagnitude / crossSectionalArea;

                // Determine color based on stress level
                float stressRatio = Math.min(stress / breakingStress, 1.0f); // Clamp between 0 and 1
                color1 = new Color(1.0f - stressRatio, 1.0f, 0.0f, 1.0f); // Green to red transition
                color2 = color1;
            }

            // Use rectLine to render the segment
            shapeRenderer.rectLine(pos1.x, pos1.y, pos2.x, pos2.y, 1.0f, color1, color2);
        }

        shapeRenderer.end();
    }


    @Override
    public void update(float delta) {
        for (int i = 0; i < stringJoints.length; i++) {
            DistanceJoint joint = stringJoints[i];
            if (joint == null || stringBodies[i] == null) continue;

            // Get the current reaction force from the joint
            Vector2 reactionForce = joint.getReactionForce(1 / GameConstants.STEP);
            float forceMagnitude = reactionForce.len(); // Convert force vector to scalar magnitude

            float stress = forceMagnitude / crossSectionalArea;

            // Break the joint if the force exceeds the weakened threshold
            if (stress > breakingStress) {
                world.destroyJoint(joint);
                stringJoints[i] = null; // Mark joint as broken
                System.out.println("Segment " + i + " broke due to accumulated fatigue! Amt:" + stress);
            }
        }
        // Interpolate the pos
        if (isPinning) {
            boolean shouldStopPinning = false;
            // Tune this for desired smoothness
            float interpolationSpeed = 5;
            if(pinTargetStart != null) {
                Vector2 currentStart = stringBodies[0].getPosition();
                Vector2 newStart = currentStart.lerp(pinTargetStart, interpolationSpeed * delta);
                stringBodies[0].setTransform(newStart, stringBodies[0].getAngle());

                if (currentStart.epsilonEquals(pinTargetStart, 4)) {
                    stringBodies[0].setType(BodyDef.BodyType.StaticBody);
                    stringBodies[0].setTransform(pinTargetStart, stringBodies[0].getAngle());
                    System.out.println("Pinning start transition complete.");
                    pinTargetStart = null;
                    shouldStopPinning = true;
                }
            }

            if(pinTargetEnd != null) {
                Vector2 currentEnd = stringBodies[stringBodies.length - 1].getPosition();
                Vector2 newEnd = currentEnd.lerp(pinTargetEnd, interpolationSpeed * delta);
                stringBodies[stringBodies.length - 1].setTransform(newEnd, stringBodies[stringBodies.length - 1].getAngle());

                if (currentEnd.epsilonEquals(pinTargetEnd, 4)) {
                    stringBodies[stringBodies.length - 1].setType(BodyDef.BodyType.StaticBody);
                    stringBodies[stringBodies.length - 1].setTransform(pinTargetEnd, stringBodies[stringBodies.length - 1].getAngle());
                    System.out.println("Pinning end transition complete.");
                    pinTargetEnd = null;
                    shouldStopPinning = true;
                } else {
                    shouldStopPinning = false;
                }
            }
            if(shouldStopPinning){
                isPinning = false;
            }
        }
    }

    @Override
    public Vector2 getPosition() {
        return stringBodies[0].getPosition();
    }

    public void attachBody(Body body) {
        attachedBody = body;
        Body lastString = stringBodies[stringBodies.length - 1];
        if (attachedBodyJoint != null) {
            world.destroyJoint(attachedBodyJoint);
            attachedBodyJoint = null;
        }
        RopeJointDef jointDef = new RopeJointDef();
        jointDef.bodyA = lastString;
        jointDef.bodyB = attachedBody;
        jointDef.maxLength = 0; // Prevent excessive stretching
        jointDef.localAnchorA.set(0, 0);
        jointDef.localAnchorB.set(0, 0);
        jointDef.collideConnected = false;

        attachedBodyJoint = (RopeJoint) world.createJoint(jointDef);
        System.out.println("Body attached: " + body);
    }

    public void pinString(Vector2 pinPos) {
        isPinning = true;
        pinTargetStart = new Vector2(pinPos);
        System.out.println("Pinned to: " + pinPos);
    }
    public void pinString(Vector2 pinPos1, Vector2 pinPos2) {
        isPinning = true;
        pinTargetStart = new Vector2(pinPos1);
        pinTargetEnd = new Vector2(pinPos2);
        System.out.println("Pinned start to: " + pinPos1);
        System.out.println("Pinned end to: " + pinPos2);
    }

    @Override
    public void dispose() {
        for (Body body : stringBodies) {
            if (body == null) continue;
            world.destroyBody(body);
        }
    }
}
