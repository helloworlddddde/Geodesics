package ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import orbital.entity.Orbiter;
import orbital.entity.Particle;
import orbital.mechanics.DataGenerator;
import orbital.mechanics.OrbitalSimulator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UIGenerator {

    public static SubScene generateEffectivePotentialSubScene(Orbiter orbiter) {
        ArrayList<double[]> effectivePotentialData = DataGenerator.generatePotentialData(3, 100, 0.3, orbiter);
        ArrayList<double[]> epsilonData = DataGenerator.generateEpsilonData(3, 100, 0.3, orbiter);
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

        Button addOrbiterButton = new Button("Add Orbiter");
        addOrbiterButton.setMinWidth(250);
        Button launchOrbiterButton = new Button("Launch Orbiters");
        launchOrbiterButton.setMinWidth(250);
        TextField stepSizeTextField = new TextField();
        stepSizeTextField.setMinWidth(125);
        stepSizeTextField.setPromptText("Δt");
        TextField turnOffsetTextField = new TextField();
        turnOffsetTextField.setMinWidth(125);
        turnOffsetTextField.setPromptText("η");
        TextField durationTextField = new TextField();
        durationTextField.setMinWidth(125);
        durationTextField.setPromptText("T");
        TextField timerTextField = new TextField();
        timerTextField.setMinWidth(125);
        timerTextField.setEditable(false);
        timerTextField.setText("t: 0");


        HBox horizontalUtilityBox = new HBox(stepSizeTextField, turnOffsetTextField, durationTextField, timerTextField);
        HBox horizontalLauncherBox = new HBox(addOrbiterButton, launchOrbiterButton);
        VBox verticalScrollableLauncherBox = new VBox();
        ScrollPane launcherScrollPane = new ScrollPane(verticalScrollableLauncherBox);
        launcherScrollPane.setMinWidth(500);
        launcherScrollPane.setMinHeight(450);

        ArrayList<Orbiter> orbiters = new ArrayList<>();
        addOrbiterButton.setOnAction(event -> {
            TextField orbiterDataTextField = new TextField(orbiter.toString());
            orbiterDataTextField.setEditable(false);
            orbiterDataTextField.setMinWidth(498);
            verticalScrollableLauncherBox.getChildren().add(orbiterDataTextField);
            orbiters.add(new Particle(orbiter));
        });


        launchOrbiterButton.setOnAction(event -> {
            double stepSize = Double.parseDouble(stepSizeTextField.getText());
            double turnOffset = Double.parseDouble(turnOffsetTextField.getText());
            double duration = Double.parseDouble(durationTextField.getText());
            RotationGroup centerPlane = (RotationGroup) simulationSubScene.getRoot();
            TableView<PointView3D> orbiterTrackerTableView = (TableView) orbiterTrackerSubScene.getRoot();
            for(Orbiter o : orbiters) {
                orbiterTrackerTableView.getItems().add(o.getGlobalPointView3D());
            }


            OrbitalSimulator orbitalSimulator = new OrbitalSimulator(orbiters, stepSize, turnOffset, duration);
            ArrayList<ArrayList<double[]>> simulationData = orbitalSimulator.getSimulationData();

            for (int i = 0; i < orbiters.size(); i++) {
                Orbiter o = orbiters.get(i);
                RotationGroup dummyOrbitalPlane = new RotationGroup();
                dummyOrbitalPlane.rotateByX(o.getRotationalOffsets()[0]);
                dummyOrbitalPlane.rotateByY(o.getRotationalOffsets()[1]);
                o.setOrbitalPlane(dummyOrbitalPlane);
                centerPlane.getChildren().add(o.getOrbitalPlane());

                o.setData(simulationData.get(i).get(0));
                o.translate();
            }

            Timer simulationTimer = new Timer("Simulation Timer");

            simulationTimer.schedule(new TimerTask() {
                int count = 0;
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (count < simulationData.get(0).size()) {
                            if (count % 10 == 0) {
                                for (int i = 0; i < simulationData.size(); i++) {
                                    Orbiter o = orbiters.get(i);
                                    o.setData(simulationData.get(i).get(count));
                                    o.translate();
                                    timerTextField.setText("t: " + stepSize * count);
                                    orbiterTrackerTableView.getItems().set(i, o.getGlobalPointView3D());
                                }
                            }
                            count++;
                        } else {
                            simulationTimer.cancel();
                        }

                    });

                }
            }, 0, 1);


        });

        VBox verticalLauncherBox = new VBox(horizontalUtilityBox, horizontalLauncherBox, launcherScrollPane);

        SubScene subScene = new SubScene(verticalLauncherBox, 500, 500);
        return subScene;
    }


    private static SubScene generateOrbiterSetupSubScene(SubScene effectivePotentialSubScene, Orbiter orbiter) {
        VBox verticalToolBox = new VBox();
        ArrayList<HBox> horizontalToolBoxes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Button inputButton = new Button();
            inputButton.setAlignment(Pos.BASELINE_LEFT);
            inputButton.setMinWidth(100);
            TextField inputTextField = new TextField();
            inputTextField.setMinWidth(400);
            HBox horizontalToolBox = new HBox(inputButton, inputTextField);

            switch (i) {
                case 0:
                    inputButton.setText("m");
                    inputTextField.setText(Double.toString(orbiter.getMass()));
                    break;
                case 1:
                    inputButton.setText("M");
                    inputTextField.setText(Double.toString(orbiter.getSchMass()));
                    break;
                case 2:
                    inputButton.setText("e");
                    inputTextField.setText(Double.toString(orbiter.getE()));
                    break;
                case 3:
                    inputButton.setText("l");
                    inputTextField.setText(Double.toString(orbiter.getL()));
                    break;
                case 4:
                    inputButton.setText("d");
                    inputTextField.setText(Integer.toString(orbiter.getDirection()));
                    break;
                case 5:
                    inputButton.setText("r");
                    inputTextField.setText(Double.toString(orbiter.getData()[1]));
                    break;
                case 6:
                    inputButton.setText("φ");
                    inputTextField.setText(Double.toString(orbiter.getData()[3]));
                    break;
                case 7:
                    inputButton.setText("R");
                    inputTextField.setText(Double.toString(100));
                    break;
                case 8:
                    inputButton.setText("X");
                    inputTextField.setText(Double.toString(orbiter.getRotationalOffsets()[0]));
                    break;
                case 9:
                    inputButton.setText("Y");
                    inputTextField.setText(Double.toString(orbiter.getRotationalOffsets()[1]));
                    break;
            }
            horizontalToolBoxes.add(horizontalToolBox);
        }


        Button updateButton = new Button("update");
        updateButton.setAlignment(Pos.BASELINE_LEFT);
        updateButton.setMinWidth(100);
        HBox submissionToolBox = new HBox(updateButton);
        verticalToolBox.getChildren().addAll(horizontalToolBoxes);
        verticalToolBox.getChildren().add(submissionToolBox);
        updateButton.setOnAction(event -> {

            double[] updatedParameters = new double[10];
            for (int i = 0; i < 10; i++) {
                TextField inputTextField = (TextField) horizontalToolBoxes.get(i).getChildren().get(1);
                String inputText = inputTextField.getText();
                updatedParameters[i] = Double.parseDouble(inputText);

            }

            orbiter.setMass(updatedParameters[0]);
            orbiter.setSchMass(updatedParameters[1]);
            orbiter.setE(updatedParameters[2]);
            orbiter.setL(updatedParameters[3]);
            orbiter.setDirection((int) updatedParameters[4]);
            orbiter.setInitialConditions(
                    new double[]{0, updatedParameters[5], Math.PI / 2, updatedParameters[6], 0},
                    new double[]{updatedParameters[8], updatedParameters[9]}
            );
            ArrayList<double[]> effectivePotentialData = DataGenerator.generatePotentialData(3, updatedParameters[7], 0.3, orbiter);
            ArrayList<double[]> epsilonData = DataGenerator.generateEpsilonData(3, updatedParameters[7], 0.3, orbiter);
            LineChart<Number, Number> effectivePotentialChart = (LineChart<Number, Number>) effectivePotentialSubScene.getRoot();

            XYChart.Series<Number, Number> effectivePotentialSeries = new XYChart.Series<Number, Number>();
            ;
            XYChart.Series<Number, Number> epsilonSeries = new XYChart.Series<Number, Number>();
            ;

            for (double[] dataPair : effectivePotentialData) {
                effectivePotentialSeries.getData().add(new XYChart.Data<Number, Number>(dataPair[0], dataPair[1]));
            }

            for (double[] dataPair : epsilonData) {
                epsilonSeries.getData().add(new XYChart.Data<Number, Number>(dataPair[0], dataPair[1]));
            }

            effectivePotentialChart.getData().set(0, effectivePotentialSeries);
            effectivePotentialChart.getData().set(1, epsilonSeries);

        });


        SubScene subScene = new SubScene(verticalToolBox, 500, 500);
        return subScene;
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

        for (TableColumn column : tableView.getColumns()) {
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
