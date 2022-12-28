package ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import orbital.data.OrbitalIntegrator;
import orbital.entity.Orbiter;
import orbital.entity.Particle;
import orbital.mechanics.DataGenerator;
import orbital.mechanics.OrbitalSimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UIGenerator {

    public static SubScene generateEffectivePotentialSubScene(Orbiter orbiter) {
        ArrayList<XYChart.Data<Number, Number>> effectivePotentialData = DataGenerator.generateEffectivePotentialData(3, 100, 0.05, orbiter);
        ArrayList<XYChart.Data<Number, Number>> epsilonData = DataGenerator.generateEpsilonData(2.75, 100, 0.05, orbiter);
        SubScene subScene = new SubScene(DataVisualizer.generateChart(effectivePotentialData, epsilonData), 500, 500);
        return subScene;
    }

    public static SubScene generateSimulationSubScene() {
        RotationGroup centerPlane = new RotationGroup();
        Box centerBox = new Box(5, 5, 5);
        centerPlane.getChildren().add(centerBox);
        SubScene subScene = new SubScene(centerPlane, 500, 500);
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
        SubScene simulationSubScene = generateSimulationSubScene();
        SubScene orbiterTrackerSubScene = generateOrbiterTrackerSubScene();
        SubScene orbiterSetupSubScene = generateOrbiterSetupSubScene(effectivePotentialSubScene, orbiter);
        SubScene orbiterLauncherSubScene = generateOrbiterLauncherSubScene(orbiter, simulationSubScene, orbiterTrackerSubScene);
        VBox rootVerticalBox = new VBox();
        HBox row1HorizontalBox = new HBox(effectivePotentialSubScene,
                simulationSubScene, orbiterTrackerSubScene);
        HBox row2HorizontalBox = new HBox(orbiterSetupSubScene,
                orbiterLauncherSubScene);
        rootVerticalBox.getChildren().addAll(row1HorizontalBox, row2HorizontalBox);
        Scene scene = new Scene(rootVerticalBox, 1500, 1000, true);
        setMainSceneControls(scene, effectivePotentialSubScene, simulationSubScene);
        return scene;
    }


    private static SubScene generateOrbiterLauncherSubScene(Orbiter orbiter, SubScene simulationSubScene, SubScene orbiterTrackerSubScene) {
        ArrayList<Orbiter> orbiters = new ArrayList<Orbiter>() {{
            add(orbiter);
        }};
        Button test = new Button("test");
        RotationGroup centralGroup = (RotationGroup) simulationSubScene.getRoot();
        centralGroup.getChildren().add(orbiter.getOrbitalPlane());
        HBox row1HorizontalBox = new HBox(test);
        VBox col1VerticalBox = new VBox(row1HorizontalBox);

        test.setOnAction(event -> {
            OrbitalIntegrator test2 = new OrbitalIntegrator(1);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {

                        test2.orbitalIntegrate(orbiter, orbiters);
                        orbiter.translate();
                        orbiter.generateTracer();


                    });
                }
            }, 0, 1);
        });
        return new SubScene(col1VerticalBox, 500, 500);
    }


    private static SubScene generateOrbiterSetupSubScene(SubScene effectivePotentialSubScene, Orbiter orbiter) {
        return new SubScene(new Group(), 500, 500);
    }

    private static SubScene generateOrbiterTrackerSubScene() {
        TableView<PointView3D> tableView = new TableView<>();
        TableColumn<PointView3D, String> xColumn = new TableColumn("x");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        TableColumn<PointView3D, String> yColumn = new TableColumn("y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        TableColumn<PointView3D, String> zColumn = new TableColumn("z");
        zColumn.setCellValueFactory(new PropertyValueFactory<>("z"));
        TableColumn<PointView3D, String> tauColumn = new TableColumn("τ");
        tauColumn.setCellValueFactory(new PropertyValueFactory<>("tau"));
        tableView.getColumns().addAll(xColumn, yColumn, zColumn, tauColumn);

        for (TableColumn column : tableView.getColumns()) {
            column.setMinWidth(124.5);
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
                case Q:
                    group.rotateByZ(-10);
                    break;
                case E:
                    group.rotateByZ(10);
                    break;
            }
        });
    }


}
