package ui;

import OrbitSolver.Light;
import OrbitSolver.Orbiter;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.*;

import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;


public class Main extends Application {
    boolean toPlot = true;
    double step = 1;
    double turnCheck = 3.0;
    RotationGroup group;


    public static void main(String args[]) throws Exception {
        launch(args);


    }
// 0.08718378
    //test.initialize(1, 10000, 20, 1.3, 0, -1, 0.01075, 1.08);
    @Override
    public void start(Stage primaryStage) throws Exception {

        Orbiter orbiter = new Orbiter();
        orbiter.initialize(1, 6000, 100, 1.3, 0, -1, 0.00041, 1.013, 100);
        //orbiter.initialize(1, 20000, 300, 1.3, 0, -1, 0.000005, 0.06, 100);

        Stage dataStage = new Stage();
        initializeDataStage(dataStage, orbiter);


    }

    private LineChart createChart(String title) {
        NumberAxis xAxis = new NumberAxis(); // we are gonna plot against time
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        lineChart.setCreateSymbols(false);
        lineChart.setMaxWidth(500);
        return lineChart;
    }

    private void initializeAnalysisStage(Stage analysisStage, Orbiter orbiter) {


        LineChart<Number, Number> rChart = createChart("radii");
        XYChart.Series<Number, Number> rSeries = new XYChart.Series<>();
        rSeries.setName("r series");
        rChart.getData().add(rSeries);
        rChart.setCreateSymbols(false);

        LineChart<Number, Number> phiChart = createChart("phi");
        XYChart.Series<Number, Number> phiSeries = new XYChart.Series<>();
        phiSeries.setName("phi series");
        phiChart.getData().add(phiSeries);
        phiChart.setCreateSymbols(false);

        LineChart<Number, Number> phi2Chart = createChart("phi  MOD 2π");
        XYChart.Series<Number, Number> phi2Series = new XYChart.Series<>();
        phi2Series.setName("phiMod series");
        phi2Chart.getData().add(phi2Series);
        phi2Chart.setCreateSymbols(false);

        LineChart<Number, Number> tChart = createChart("t");
        XYChart.Series<Number, Number> tSeries = new XYChart.Series<>();
        tSeries.setName("t series");
        tChart.getData().add(tSeries);
        tChart.setCreateSymbols(false);


        for (int k = 0; k < orbiter.getSize(); k += 4 / step) {
            rSeries.getData().add(new XYChart.Data(k * step, orbiter.getR()[k]));
            phiSeries.getData().add(new XYChart.Data(k * step, orbiter.getPhi()[k]));
            phi2Series.getData().add(new XYChart.Data(k * step, orbiter.getPhi()[k] % (2*Math.PI)));
            tSeries.getData().add(new XYChart.Data(k * step, orbiter.getT()[k]));
        }

        VBox vBox = new VBox(rChart, phiChart, phi2Chart, tChart);
        Scene analysisScene = new Scene(vBox, 500, 500);
        analysisStage.setScene(analysisScene);
        analysisStage.show();


    }

    private void initializeSimulationStage(Stage simulationStage, Orbiter orbiter) {
        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(2000);
        camera.translateZProperty().set(-1300);
        group = new RotationGroup();
        Box center = new Box(6, 6, 6);
        center.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
        group.getChildren().addAll(orbiter, center);
        Scene simulationScene = new Scene(group, 800, 800, true);
        simulationStage.setScene(simulationScene);
        simulationScene.setCamera(camera);
        simulationScene.setFill(Color.SALMON);
        simulationScene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Z:
                    camera.translateZProperty().set(camera.getTranslateZ() + 300.0);
                    break;
                case X:
                    camera.translateZProperty().set(camera.getTranslateZ() - 300.0);
                    break;
                case A:
                    group.rotateByY(10);
                    break;
                case D:
                    group.rotateByY(-10);
                    break;
                case W:
                    group.rotateByX(10);
                    break;
                case S:
                    group.rotateByX(-10);
                    break;

            }
        });
        simulationStage.show();


    }

    private void initializeDataStage(Stage dataStage, Orbiter orbiter) {
        LineChart<Number, Number> potentialPlot = orbiter.plot();
        VBox vBox = new VBox(potentialPlot);
        Scene dataScene = new Scene(vBox, 500, 500);
        dataStage.setScene(dataScene);
        TextArea dataText = new TextArea(orbiter.getDataText());
        dataText.setEditable(false);
        String[] labels = new String[]{"M", "size", "r0", "phi0", "t0", "direction", "dphi0", "dt0", "plotMax"};
        for (int i = 0; i < 9; i++) {
            Button button = new Button(labels[i]);
            button.setMinSize(100, 25);
            button.setAlignment(Pos.BASELINE_LEFT);
            TextField textField = new TextField();
            HBox hBox = new HBox(button, textField);
            vBox.getChildren().add(hBox);

            int finalI = i;


            button.setOnAction((event -> {
                switch (finalI) {
                    case (0):
                        orbiter.setM(Double.parseDouble(textField.getText()));
                        break;
                    case (1):
                        orbiter.setSize(Integer.parseInt(textField.getText()));
                        break;
                    case (2):
                        orbiter.getR()[0] = Double.parseDouble(textField.getText());
                        orbiter.setL(orbiter.getR()[0] * orbiter.getR()[0] * Double.parseDouble(textField.getText()));
                        orbiter.setE((1 - 2 * orbiter.getM() / orbiter.getR()[0]) * Double.parseDouble(textField.getText()));
                        break;
                    case (3):
                        orbiter.getPhi()[0] = Double.parseDouble(textField.getText());
                        break;
                    case (4):
                        orbiter.getT()[0] = Double.parseDouble(textField.getText());
                        break;
                    case (5):
                        orbiter.setDirection(Integer.parseInt(textField.getText()));
                        break;
                    case (6):
                        orbiter.setL(orbiter.getR()[0] * orbiter.getR()[0] * Double.parseDouble(textField.getText()));
                        break;
                    case (7):
                        orbiter.setE((1 - 2 * orbiter.getM() / orbiter.getR()[0]) * Double.parseDouble(textField.getText()));
                        break;
                    case (8):
                        orbiter.setPlotMax(Double.parseDouble(textField.getText()));
                        break;
                }

                vBox.getChildren().remove(0);
                vBox.getChildren().add(0, orbiter.plot());
                dataText.setText(orbiter.getDataText());


            }));


        }


        Button runButton = new Button("run");
        runButton.setOnAction(event -> {
            runOrbiter(orbiter);
        });
        vBox.getChildren().add(dataText);
        vBox.getChildren().add(runButton);


        dataStage.show();

    }

    private void runOrbiter(Orbiter orbiter) {
        Stage simulationStage = new Stage();
        initializeSimulationStage(simulationStage, orbiter);


        orbiter.RungeKutta(step, turnCheck);
        double[] r = orbiter.getR();
        double[] phi = orbiter.getPhi();




        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            Stage analysisStage = new Stage();
            int t = 0;


            @Override
            public void run() {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        if (t >= r.length || r[t] <= 0) {

                            timer.cancel();
                            simulationStage.close();
                            if (toPlot) {
                                initializeAnalysisStage(analysisStage, orbiter);
                            }


                        } else {
                            if (t % 10 == 0) {
                                orbiter.translateXProperty().set(r[t] * Math.cos(phi[t]));
                                orbiter.translateYProperty().set(r[t] * Math.sin(phi[t]));
                                Circle tracer = new Circle(1);
                                tracer.setFill(Color.TURQUOISE);
                                tracer.setCenterX(r[t] * Math.cos(phi[t]));
                                tracer.setCenterY(r[t] * Math.sin(phi[t]));
                                group.getChildren().add(tracer);
                            }
                            t++;
                        }


                    }
                });

            }
        }, 0, 1);
    }
}









