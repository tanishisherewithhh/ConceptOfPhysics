package com.cop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.cop.objects.Ball;
import com.cop.objects.Object_;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.TableUtils;
import com.kotcrab.vis.ui.widget.*;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

import static com.cop.GameConstants.world;

public class ParametersVisUI extends ApplicationAdapter{
    private MenuBar menuBar;
    private Stage stage;
    ParameterWindow parameterWindow;
    ConceptOfPhysicsMain main;

    public ParametersVisUI(ConceptOfPhysicsMain main){
        this.main = main;
    }

    @Override
    public void create() {
        VisUI.setSkipGdxVersionCheck(true);
        VisUI.load(VisUI.SkinScale.X1);

        stage = new Stage(GameConstants.viewport);
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        menuBar = new MenuBar();
        root.add(menuBar.getTable()).growX().row();
        root.add().grow();

        parameterWindow = new ParameterWindow();
        createMenus();
    }

    private void createMenus() {
        Menu options = new Menu("Options");

        options.addItem(new MenuItem("ParameterWindow", new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                stage.addActor(parameterWindow);
            }
        }));
        options.addItem(new MenuItem("Add Ball", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Vector2 random = Vector2.Zero.setToRandomDirection();
                random.scl(5);
                main.objects.add(new Ball(GameConstants.viewport.getWorldWidth()/2, GameConstants.viewport.getWorldHeight()/2, 20f, world, random));
            }
        }));
        options.addItem(new MenuItem("Remove last Ball", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!main.objects.isEmpty()) {
                    Object_ obj = main.objects.removeLast();
                    world.destroyBody(obj.getBody());
                }
            }
        }));
        options.addItem(new MenuItem("Show Velocity Arrow", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Ball.showVelocityArrow = !Ball.showVelocityArrow;
            }
        }));

        menuBar.addMenu(options);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render () {
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void dispose () {
        VisUI.dispose();
        stage.dispose();
    }

    public static class ParameterWindow extends VisWindow{
        public VisSlider gravity = new VisSlider(0, 10, 0.01f, false);
        public VisSlider collisionDamping = new VisSlider(0, 1, 0.001f, false);
        public VisSlider airFriction = new VisSlider(0, 0.5f, 0.01f, false);
        public VisSlider radius = new VisSlider(0, 10, 0.5f, false);
        public Color color;

        public ParameterWindow() {
            super("Parameters", true);

            TableUtils.setSpacingDefaults(this);
            columnDefaults(0).left();

            addCloseButton();
            closeOnEscape();

            gravity.setValue(0.10f);
            collisionDamping.setValue(0.8f);
            airFriction.setValue(0f);
            radius.setValue(2f);

            VisLabel gravityValueLabel = new VisLabel("" + gravity.getValue());
            gravity.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    world.setGravity(new Vector2(0, -gravity.getValue()));
                    String s = String.format("%.2f",gravity.getValue());
                    gravityValueLabel.setText(s);
                }
            });
            gravityValueLabel.setAlignment(Align.left); // Align text to the right
            gravityValueLabel.setWidth(50f); // Set fixed width for the label
            gravityValueLabel.setEllipsis(false);

            VisLabel dampingValueLabel = new VisLabel("" + collisionDamping.getValue());
            collisionDamping.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String s = String.format("%.3f",collisionDamping.getValue());
                    dampingValueLabel.setText(s);
                }
            });
            dampingValueLabel.setAlignment(Align.left);
            dampingValueLabel.setWidth(50f);
            dampingValueLabel.setEllipsis(false);


            VisLabel airFrictionValueLabel = new VisLabel("" + airFriction.getValue());
            airFriction.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String s = String.format("%.2f",airFriction.getValue());
                    airFrictionValueLabel.setText(s);
                }
            });
            airFrictionValueLabel.setAlignment(Align.left);
            airFrictionValueLabel.setWidth(50f);
            airFrictionValueLabel.setEllipsis(false);

            VisLabel radiusValueLabel = new VisLabel("" + radius.getValue());
            radius.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    String s = String.format("%.1f",radius.getValue());
                    radiusValueLabel.setText(s);
                }
            });
            radiusValueLabel.setAlignment(Align.left);
            radiusValueLabel.setWidth(50f);
            radiusValueLabel.setEllipsis(false);

            VisTable sliderTable = new VisTable(true);

            VisTable gravityRow = new VisTable(true);
            gravityRow.add(new VisLabel("Gravity")).padRight(10f);
            gravityRow.add(gravity).padRight(10f);
            gravityRow.add(gravityValueLabel).width(50f);
            sliderTable.add(gravityRow).row();

            VisTable dampingRow = new VisTable(true);
            dampingRow.add(new VisLabel("Collision Damping")).padRight(10f);
            dampingRow.add(collisionDamping).padRight(10f);
            dampingRow.add(dampingValueLabel).width(50f);
            sliderTable.add(dampingRow).row();

            VisTable airFrictionRow = new VisTable(true);
            airFrictionRow.add(new VisLabel("Air Friction")).padRight(10f);
            airFrictionRow.add(airFriction).padRight(10f);
            airFrictionRow.add(airFrictionValueLabel).width(50f);
            sliderTable.add(airFrictionRow).row();

            VisTable radiusRow = new VisTable(true);
            radiusRow.add(new VisLabel("Radius")).padRight(10f);
            radiusRow.add(radius).padRight(10f);
            radiusRow.add(radiusValueLabel).width(50f);
            sliderTable.add(radiusRow).row();

            final ColorPicker picker = new ColorPicker("Ball Color", new ColorPickerAdapter() {
                @Override
                public void finished(Color newColor) {
                    if(newColor == null) return;
                    color = newColor;
                }
            });

            VisTextButton showPickerButton = new VisTextButton("Pick Color");
            showPickerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    getStage().addActor(picker.fadeIn());
                }
            });

            picker.setColor(Color.WHITE);

            VisTable pickerTable = new VisTable(true);
            pickerTable.add(showPickerButton);

            add(sliderTable).row();
            add(pickerTable).padBottom(3f);

            pack();
            centerWindow();
        }
    }

    public ParameterWindow getParameterWindow() {
        return parameterWindow;
    }
}

