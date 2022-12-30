package ui;

import javafx.application.Platform;
import javafx.geometry.Point3D;
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
import javafx.scene.shape.Sphere;
import orbital.data.OrbitalData;
import orbital.data.OrbitalIntegrator;
import orbital.entity.Orbiter;
import orbital.entity.Particle;
import orbital.entity.Tracer;
import orbital.mechanics.DataGenerator;
import orbital.mechanics.OrbitalSimulator;

import java.lang.reflect.Array;
import java.util.*;

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


        ArrayList<Orbiter> orbiters = new ArrayList<>();

        VBox verticalScrollPaneContent = new VBox();

        ScrollPane verticalScrollPane = new ScrollPane(verticalScrollPaneContent);

        Button launchOrbiterButton = new Button("Launch Orbiters") {{
           setMinWidth(125);
           setOnAction(event -> {

               renderOrbit(orbiters, simulationSubScene, orbiterTrackerSubScene);

           });
        }};

        Button addOrbiterButton = new Button("Add Orbiter") {{
            setMinWidth(125);
            setOnAction(event -> {

                orbiter.getOrbitalData().setRotationalData(
                        new double[]{Math.random() * 360, Math.random() * 360, Math.random() * 360}
                );
                Orbiter newOrbiter = new Particle(orbiter);
                orbiters.add(newOrbiter);

                TextArea orbiterDetailsTextArea = new TextArea(newOrbiter.getOrbitalData().toString()) {{
                    setMinWidth(498);
                    setMaxHeight(100);
                    setEditable(false);
                }};
                verticalScrollPaneContent.getChildren().add(orbiterDetailsTextArea);


            });
        }};

        HBox row1HorizontalBox = new HBox(launchOrbiterButton, addOrbiterButton);

        VBox col1VerticalBox = new VBox(row1HorizontalBox, verticalScrollPane);


        return new SubScene(col1VerticalBox, 500, 500);
    }


    private static SubScene generateOrbiterSetupSubScene(SubScene effectivePotentialSubScene, Orbiter orbiter) {
        return new SubScene(new Group(), 500, 500);
    }

    private static void renderOrbit(ArrayList<Orbiter> orbiters, SubScene simulationSubScene, SubScene orbiterTrackerSubScene) {

        TableView<PointView3D> tableView = (TableView) orbiterTrackerSubScene.getRoot();

        tableView.getItems().clear();

        for(Orbiter o : orbiters) {
            tableView.getItems().add(new PointView3D(o.getGlobalCartesianCoordinates()));
        }
        RotationGroup centerPlane = (RotationGroup) simulationSubScene.getRoot();

        OrbitalSimulator orbitalSimulator = new OrbitalSimulator(orbiters, 0.1, 15000);

        ArrayList<ArrayList<OrbitalData>> simulationData = orbitalSimulator.getSimulationData();

        ArrayList<ArrayList<Point3D>> coordinateData = orbitalSimulator.getCoordinatesData();

        for(Orbiter o : orbiters) {
            if (!centerPlane.getChildren().contains(o.getOrbitalPlane())) {
                centerPlane.getChildren().add(o.getOrbitalPlane());
            }
        }

        for(Node node : centerPlane.getChildren()) {
            if (node instanceof Group) {
                ((Group) node).getChildren().removeIf((n) -> n instanceof Tracer);
            }
        }


        new Timer("Simulation Timer") {{
           schedule(new TimerTask() {
               int count = 0;
               final int updatesPerMs = 10;
               @Override
               public void run() {
                   Platform.runLater(() -> {
                       if (count < simulationData.get(0).size()) {
                           for(int i = 0; i < orbiters.size(); i++) {
                               Orbiter o = orbiters.get(i);
                               o.setOrbitalData(simulationData.get(i).get(count));
                               o.translate();
                               o.generateTracer();
                               tableView.getItems().set(i, new PointView3D(o.getGlobalCartesianCoordinates()));
                           }

                           count += updatesPerMs;

                       } else {
                           cancel();
                           for(int i = 0; i < orbiters.size(); i++) {
                               Orbiter o = orbiters.get(i);
                               OrbitalData initialOData = simulationData.get(i).get(0);
                               o.setOrbitalData(initialOData);
                           }
                       }
                   });
               }
           }, 0, 1);
        }};

    }

    private static SubScene generateOrbiterTrackerSubScene() {
        TableView<PointView3D> tableView = new TableView<>();
        TableColumn<PointView3D, String> xColumn = new TableColumn("x");
        xColumn.setCellValueFactory(new PropertyValueFactory<>("x"));
        TableColumn<PointView3D, String> yColumn = new TableColumn("y");
        yColumn.setCellValueFactory(new PropertyValueFactory<>("y"));
        TableColumn<PointView3D, String> zColumn = new TableColumn("z");
        zColumn.setCellValueFactory(new PropertyValueFactory<>("z"));
        tableView.getColumns().addAll(xColumn, yColumn, zColumn);

        for (TableColumn column : tableView.getColumns()) {
            column.setMinWidth(500.0 / 3);
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
