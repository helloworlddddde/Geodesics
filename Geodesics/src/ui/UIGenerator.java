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
        ArrayList<double[]> effectivePotentialData = DataGenerator.generateEffectivePotentialData(2.75, 100, 0.05, orbiter);
        ArrayList<double[]> epsilonData = DataGenerator.generateEpsilonData(2.75, 100, 0.05, orbiter);
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
        addOrbiterButton.setMinWidth(125);
        Button launchOrbiterButton = new Button("Launch Orbiters");
        launchOrbiterButton.setMinWidth(125);
        Button removeAllOrbitersButton = new Button("Remove All Orbiters");
        removeAllOrbitersButton.setMinWidth(250);
        TextField globalStepSizeTextField = new TextField();
        globalStepSizeTextField.setMinWidth(125);
        globalStepSizeTextField.setPromptText("Δt");
        globalStepSizeTextField.setText("0.2");
        TextField durationTextField = new TextField();
        durationTextField.setMinWidth(125);
        durationTextField.setPromptText("T");
        durationTextField.setText("20000");
        TextField timerTextField = new TextField();
        timerTextField.setMinWidth(125);
        timerTextField.setEditable(false);
        timerTextField.setText("ΔT: " + 0);
        TextField playbackTimeResolutionTextField = new TextField();
        playbackTimeResolutionTextField.setMinWidth(125);
        playbackTimeResolutionTextField.setPromptText("Ω");
        playbackTimeResolutionTextField.setText("10");


        HBox horizontalUtilityBox = new HBox(globalStepSizeTextField, durationTextField, timerTextField, playbackTimeResolutionTextField);
        HBox horizontalLauncherBox = new HBox(launchOrbiterButton, addOrbiterButton, removeAllOrbitersButton);
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

        removeAllOrbitersButton.setOnAction(event -> {
           orbiters.clear();
           verticalScrollableLauncherBox.getChildren().clear();
        });



        launchOrbiterButton.setOnAction(event -> {
            double globalStepSize = Double.parseDouble(globalStepSizeTextField.getText());
            double duration = Double.parseDouble(durationTextField.getText());
            int playbackTimeResolution = Integer.parseInt(playbackTimeResolutionTextField.getText());
            RotationGroup centerPlane = (RotationGroup) simulationSubScene.getRoot();
            TableView<PointView3D> orbiterTrackerTableView = (TableView) orbiterTrackerSubScene.getRoot();
            for(Orbiter o : orbiters) {
                orbiterTrackerTableView.getItems().add(o.getGlobalPointView3D());
            }


            OrbitalSimulator orbitalSimulator = new OrbitalSimulator(true, orbiters, globalStepSize, duration);
            ArrayList<ArrayList<double[]>> simulationData = orbitalSimulator.getSimulationData();

            for (int i = 0; i < orbiters.size(); i++) {
                Orbiter o = orbiters.get(i);
                RotationGroup dummyOrbitalPlane = new RotationGroup();
                dummyOrbitalPlane.rotateByX(o.getRotationalOffsets()[0]);
                dummyOrbitalPlane.rotateByY(o.getRotationalOffsets()[1]);
                dummyOrbitalPlane.rotateByZ(o.getRotationalOffsets()[2]);
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
                            for (int i = 0; i < simulationData.size(); i++) {
                                Orbiter o = orbiters.get(i);
                                o.setData(simulationData.get(i).get(count));
                                o.translate();
                                orbiterTrackerTableView.getItems().set(i, o.getGlobalPointView3D());
                            }
                            timerTextField.setText("ΔT: " + count * globalStepSize);
                            count += playbackTimeResolution;
                        } else {
                            simulationTimer.cancel();
                            centerPlane.getChildren().removeIf(child ->
                                    !(child instanceof Box)
                            );

                            for(int i = 0; i < orbiters.size(); i++) {
                                Orbiter o = orbiters.get(i);
                                o.setData(simulationData.get(i).get(0));
                                o = new Particle(o);
                            }
                            timerTextField.setText("ΔT: " + 0);
                            orbiterTrackerTableView.getItems().clear();
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
        for (int i = 0; i < 13; i++) {
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
                case 10:
                    inputButton.setText("Z");
                    inputTextField.setText(Double.toString(orbiter.getRotationalOffsets()[2]));
                    break;
                case 11:
                    inputButton.setText("Δτ");
                    inputTextField.setText(Double.toString(orbiter.getStepSize()));
                    break;
                case 12:
                    inputButton.setText("RKn");
                    inputTextField.setText(Integer.toString(orbiter.getIntegratorOrder()));
                    break;
            }
            horizontalToolBoxes.add(horizontalToolBox);
        }


        Button updateButton = new Button("update");
        updateButton.setAlignment(Pos.BASELINE_CENTER);
        updateButton.setMinWidth(100);

        TextField indicateMinimumETextField = new TextField("є: " + String.format("%6.4e", orbiter.getMinimumE()));
        indicateMinimumETextField.setMinWidth(100);
        indicateMinimumETextField.setEditable(false);


        Button markCriticalPointsButton = new Button("mark radii");
        markCriticalPointsButton.setMinWidth(100);
        TextField markCriticalPointsTextField = new TextField(String.format("%6.4e", -1.0) + ", " + String.format("%6.4e", -1.0));
        markCriticalPointsTextField.setMinWidth(200);
        markCriticalPointsTextField.setEditable(false);
        markCriticalPointsButton.setOnAction(event -> {

            LineChart<Number, Number> effectivePotentialChart = (LineChart<Number, Number>) effectivePotentialSubScene.getRoot();
            XYChart.Series<Number, Number> effectivePotentialSeries = effectivePotentialChart.getData().get(0);
            double rMax = -1;
            double rMin = -1;
            for(int i = 0; i < effectivePotentialSeries.getData().size() - 2; i++) {
                XYChart.Data<Number, Number> prev = effectivePotentialSeries.getData().get(i);
                XYChart.Data<Number, Number> curr = effectivePotentialSeries.getData().get(i+1);
                XYChart.Data<Number, Number> next = effectivePotentialSeries.getData().get(i+2);

                if (((double) prev.getYValue() <= (double) curr.getYValue() && (double) curr.getYValue() >= (double) next.getYValue())) {
                    rMax = (double) curr.getXValue();
                }

                if (((double) prev.getYValue() >= (double) curr.getYValue() && (double) curr.getYValue() <= (double) next.getYValue())) {
                    rMin = (double) curr.getXValue();
                }
            }

            markCriticalPointsTextField.setText(String.format("%6.4e", rMax) + ", " + String.format("%6.4e", rMin));

        });

        HBox submissionToolBox = new HBox(updateButton, indicateMinimumETextField, markCriticalPointsButton, markCriticalPointsTextField);

        verticalToolBox.getChildren().addAll(horizontalToolBoxes);
        verticalToolBox.getChildren().add(submissionToolBox);

        updateButton.setOnAction(event -> {


            double[] updatedParameters = new double[13];
            for (int i = 0; i < 13; i++) {
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
                    new double[]{updatedParameters[8], updatedParameters[9], updatedParameters[10]}
            );
            orbiter.setStepSize(updatedParameters[11]);
            orbiter.setOrbitalIntegrator((int) updatedParameters[12]);
            ArrayList<double[]> effectivePotentialData = DataGenerator.generateEffectivePotentialData(2.75, updatedParameters[7], 0.05, orbiter);
            ArrayList<double[]> epsilonData = DataGenerator.generateEpsilonData(2.75, updatedParameters[7], 0.05, orbiter);
            LineChart<Number, Number> effectivePotentialChart = (LineChart<Number, Number>) effectivePotentialSubScene.getRoot();

            XYChart.Series<Number, Number> effectivePotentialSeries = new XYChart.Series<Number, Number>();

            XYChart.Series<Number, Number> epsilonSeries = new XYChart.Series<Number, Number>();


            for (double[] dataPair : effectivePotentialData) {
                effectivePotentialSeries.getData().add(new XYChart.Data<Number, Number>(dataPair[0], dataPair[1]));
            }

            for (double[] dataPair : epsilonData) {
                epsilonSeries.getData().add(new XYChart.Data<Number, Number>(dataPair[0], dataPair[1]));
            }

            effectivePotentialChart.getData().set(0, effectivePotentialSeries);
            effectivePotentialChart.getData().set(1, epsilonSeries);

            indicateMinimumETextField.setText("є: " + String.format("%6.4e", orbiter.getMinimumE()));

        });

        SubScene subScene = new SubScene(verticalToolBox, 500, 500);
        return subScene;
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
