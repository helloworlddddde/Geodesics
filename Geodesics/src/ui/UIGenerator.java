package ui;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Sphere;
import orbital.entity.Orbiter;
import orbital.entity.Particle;
import orbital.mechanics.DataGenerator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UIGenerator {

    public static SubScene generateEffectivePotentialSubScene(Orbiter orbiter) {
        VBox verticalBox = new VBox();
        ArrayList<double[]> effectivePotentialData = DataGenerator.generatePotentialData(3, 100, 0.3, orbiter);
        ArrayList<double[]> epsilonData = DataGenerator.generateEpsilonData(3, 100, 0.3, orbiter);
        verticalBox.getChildren().add(DataVisualizer.generateChart(effectivePotentialData, epsilonData));
        SubScene subScene = new SubScene(verticalBox, 500, 500);
        return subScene;
    }

    public static SubScene generateSimulationSubScene(Orbiter orbiter) {
        RotationGroup rotationGroup = new RotationGroup();
        Box centerBox = new Box(5, 5, 5);
        rotationGroup.getChildren().add(centerBox);
        SubScene subScene = new SubScene(rotationGroup, 500, 500);
        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(2000);
        camera.setTranslateZ(-1300);
        subScene.setCamera(camera);
        subScene.setFill(Color.SALMON);

        return subScene;
    }

    public static Scene generateMainScene(Orbiter orbiter) {
        SubScene effectivePotentialSubScene = generateEffectivePotentialSubScene(orbiter);
        SubScene simulationSubScene = generateSimulationSubScene(orbiter);
        Button runButton = new Button("run");
        runButton.setOnAction(event -> {
            Orbiter newOrbiter;
            if (orbiter instanceof Particle) {
                newOrbiter = new Particle(orbiter);
            } else {
                newOrbiter = new Particle(orbiter);
            }
            runOrbiter(newOrbiter, simulationSubScene, 1.0, 3.0);
        });
        HBox rootHorizontalBox = new HBox(effectivePotentialSubScene, simulationSubScene, runButton);
        Scene scene = new Scene(rootHorizontalBox, 1400, 800, true);
        setMainSceneControls(scene, effectivePotentialSubScene, simulationSubScene);
        return scene;
    }

    private static void setMainSceneControls(Scene scene, SubScene effectivePotentialSubScene, SubScene simulationSubScene) {
        Camera camera = simulationSubScene.getCamera();
        RotationGroup group = (RotationGroup) simulationSubScene.getRoot();
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z:
                    camera.translateZProperty().set(camera.getTranslateZ() - 300.0);
                    break;
                case X:
                    camera.translateZProperty().set(camera.getTranslateZ() + 300.0);
                    break;
                case A:
                    group.rotateByY(10);
                    break;
                case D:
                    group.rotateByY(-10);
                    break;
                case W:
                    group.rotateByX(-10);
                    break;
                case S:
                    group.rotateByX(10);
                    break;
            }
        });
    }

    public static Point3D getVisualCartesianCoordinates(Shape orbiterShape) {
        Bounds boundsInScene = orbiterShape.localToScene(orbiterShape.getBoundsInLocal());
        double x = (boundsInScene.getMaxX() + boundsInScene.getMinX())/2;
        double y = (boundsInScene.getMaxY() + boundsInScene.getMinY())/2;
        double z = (boundsInScene.getMaxZ() + boundsInScene.getMinZ())/2;
        return new Point3D(x, y, z);
    }


    public static void runOrbiter(Orbiter newOrbiter, SubScene subScene, double stepSize, double turnOffset) {
        Color orbiterBoxColor = Color.color(Math.random(), Math.random(), Math.random());
        Box orbiterBox = new Box(10, 10, 10);
        Material orbiterBoxMaterial = new PhongMaterial(orbiterBoxColor);
        orbiterBox.setMaterial(orbiterBoxMaterial);
        Color tracerColor = Color.color(Math.random(), Math.random(), Math.random());
        RotationGroup centerGroup = (RotationGroup) subScene.getRoot();
        RotationGroup newGroup = new RotationGroup();
        newGroup.rotateByX(360 * Math.random());
        newGroup.getChildren().add(orbiterBox);
        centerGroup.getChildren().add(newGroup);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int t = 0;
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (t == 20000) {
                        timer.cancel();
                        centerGroup.getChildren().remove(newGroup);
                    }
                    if (t % 10 == 0) {
                        double r = newOrbiter.getData()[1];
                        double phi = newOrbiter.getData()[3];
                        orbiterBox.setTranslateX(r * Math.cos(phi));
                        orbiterBox.setTranslateY(r * Math.sin(phi));
                        Sphere tracer = new Sphere(1);
                        tracer.setTranslateX(r * Math.cos(phi));
                        tracer.setTranslateY(r * Math.sin(phi));
                        Material tracerMaterial = new PhongMaterial(tracerColor);
                        tracer.setMaterial(tracerMaterial);
                        newGroup.getChildren().add(tracer);
                    }
                    newOrbiter.rungeKutta(stepSize, turnOffset);
                    t++;
                });
            }
        }, 0, 1);
    }


}
