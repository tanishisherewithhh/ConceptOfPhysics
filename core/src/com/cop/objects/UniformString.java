package com.cop.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.cop.GameConstants;

public class UniformString extends Object_ {
    private Body[] stringBodies;
    private DistanceJoint[] stringJoints;
    private float width, height, massPerLength;
    private float breakingStress; //In newtons normally
    private float crossSectionalArea = 0.0001f; // 1 cm² cross-section
    private boolean highlightStress = true; // Toggle this as needed

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

        // Connect bodies using distance joints
        for (int i = 1; i <= segmentCount; i++) {
            DistanceJointDef jointDef = new DistanceJointDef();
            jointDef.bodyA = stringBodies[i - 1];
            jointDef.bodyB = stringBodies[i];
            jointDef.length = segmentWidth; // Maintain uniform spacing
            jointDef.collideConnected = false;
            jointDef.frequencyHz = 0f;
            jointDef.dampingRatio = 0.7f;

            stringJoints[i - 1] = (DistanceJoint) world.createJoint(jointDef);
        }
    }

    @Override
    public void render(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int i = 0; i < stringBodies.length - 1; i++) {
            if (stringJoints[i] == null) {
                // Check if joints before and after are also null
                boolean isolated = i == stringJoints.length - 1 || stringJoints[i + 1] == null;

                if (isolated) {
                    Vector2 pos = stringBodies[i].getPosition();
                    float size = 1.0f; // Define the rectangle size

                    shapeRenderer.setColor(Color.LIGHT_GRAY); // Default color for isolated segments
                    shapeRenderer.rect(pos.x - size, pos.y - size / 2, size, size);
                }

                continue;
            }

            Vector2 pos1 = stringBodies[i].getPosition();
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
            if (joint == null) continue;

            // Get the current reaction force from the joint
            Vector2 reactionForce = joint.getReactionForce(1/GameConstants.STEP);
            float forceMagnitude = reactionForce.len(); // Convert force vector to scalar magnitude

            float stress = forceMagnitude / crossSectionalArea;

            // Break the joint if the force exceeds the weakened threshold
            if (stress > breakingStress) {
                world.destroyJoint(joint);
                stringJoints[i] = null; // Mark joint as broken
                System.out.println("Segment " + i + " broke due to accumulated fatigue! Amt:" + stress);
            }
        }
    }

    // Catmull-Rom interpolation function
    private float catmullRomSpline(float p0, float p1, float p2, float p3, float t) {
        float t2 = t * t;
        float t3 = t2 * t;
        return 0.5f * (
                (2 * p1) +
                        (-p0 + p2) * t +
                        (2 * p0 - 5 * p1 + 4 * p2 - p3) * t2 +
                        (-p0 + 3 * p1 - 3 * p2 + p3) * t3
        );
    }


    @Override
    public void dispose() {
        for (Body body : stringBodies) {
            if(body == null) continue;
            world.destroyBody(body);
        }
    }
}
