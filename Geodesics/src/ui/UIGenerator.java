package ui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
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
        SubScene orbiterTrackerSubScene = generateOrbiterTrackerSubScene();
        Button runButton = new Button("run");
        ArrayList<Orbiter> orbiters = new ArrayList<>();
        orbiters.add(orbiter);
        RotationGroup center = (RotationGroup) simulationSubScene.getRoot();
        RotationGroup group = new RotationGroup();
        Orbiter orbiter2 = new Particle(orbiter);
        orbiters.add(orbiter2);
        //orbiter2.setInitialConditions(new double[]{0, 50, 0, 1.3, 5}, new double[]{0, 0});
        orbiter2.setInitialConditions(new double[]{0, 100, 0, 0, 0}, new double[]{0, 0});
        RotationGroup group2 = new RotationGroup();
        group2.rotateByX(90);
        group2.getChildren().add(orbiter2);
        center.getChildren().add(group2);
        group.getChildren().add(orbiter);
        center.getChildren().add(group);
        TableView table = (TableView) orbiterTrackerSubScene.getRoot();
        table.getItems().add(orbiter.getGlobalPointView3D());
        table.getItems().add(orbiter2.getGlobalPointView3D());
        runButton.setOnAction(event -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                Double systemTime = 0.0;
                Double stepSize = 1.0;
                Double turnOffset = 3.0;
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (systemTime == 10000) {
                            timer.cancel();
                        } else {
                            int i = 0;
                            for(Orbiter o : orbiters) {
                                double r = o.getData()[1];
                                double phi = o.getData()[3];

                                o.rungeKutta(stepSize, turnOffset, orbiters);
                                table.getItems().set(i, o.getGlobalPointView3D());
                                i++;
                                systemTime += stepSize;

                        }


                        }


                    });
                }
            }, 0, 1);
        });




        VBox rootVerticalBox = new VBox();
        HBox rootHorizontalBox = new HBox(effectivePotentialSubScene,
                simulationSubScene, orbiterTrackerSubScene);
        rootVerticalBox.getChildren().addAll(rootHorizontalBox, runButton);
        Scene scene = new Scene(rootVerticalBox, 1500, 1000, true);
        setMainSceneControls(scene, effectivePotentialSubScene, simulationSubScene);
        return scene;
    }

    private static SubScene generateOrbiterTrackerSubScene() {
        TableView<PointView3D> tableView = new TableView<>();
        TableColumn<PointView3D, String> labelColumn = new TableColumn("Label");
        labelColumn.setCellValueFactory(new PropertyValueFactory<>("label"));
        TableColumn<PointView3D, String> xColumn = new TableColumn("x");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        TableColumn<PointView3D, String> yColumn = new TableColumn("y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        TableColumn<PointView3D, String> zColumn = new TableColumn("z");
        zColumn.setCellValueFactory(new PropertyValueFactory<>("z"));
        TableColumn<PointView3D, String> tauColumn = new TableColumn("τ");
        tauColumn.setCellValueFactory(new PropertyValueFactory<>("tau"));
        tableView.getColumns().addAll(labelColumn, xColumn, yColumn, zColumn, tauColumn);

        for(TableColumn column : tableView.getColumns()) {
            column.setMinWidth(100);
        }
        SubScene subScene = new SubScene(tableView, 500, 500);
        return subScene;

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







}
