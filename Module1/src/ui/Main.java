package ui;

import OrbitSolver.Orbiter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class Main extends Application {
    boolean randomTheta = false;
    boolean toPlot = false;
    double step = 1;
    double turnCheck = 3.0;
    RotationGroup group = new RotationGroup();


    public static void main(String args[]) throws Exception {
        launch(args);
    }

    // 0.08718378
    //test.initialize(1, 10000, 20, 1.3, 0, -1, 0.01075, 1.08);
    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage dataStage = new Stage();
        initializeDataStage(dataStage);


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
            phi2Series.getData().add(new XYChart.Data(k * step, orbiter.getPhi()[k] % (2 * Math.PI)));
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

    public static class DataValue {

        private final SimpleStringProperty label;
        private final SimpleStringProperty data;

        private DataValue(String label, String data) {
            this.label = new SimpleStringProperty(label);
            this.data = new SimpleStringProperty(data);
        }

        public String getLabel() {
            return label.get();
        }

        public void setLabel(String label) {
            this.label.set(label);
        }

        public String getData() {
            return data.get();
        }

        public void setData(String data) {
            this.data.set(data);
        }
    }

    private void initializeDataStage(Stage dataStage) {

        Orbiter orbiter = new Orbiter();

        orbiter.initialize(1, 6000, 100, 1.3, 0, -1, 0.00041, 1.013, 100);

        //orbiter.initialize(1, 20000, 300, 1.3, 0, -1, 0.000005, 0.06, 100);

        LineChart<Number, Number> potentialPlot = orbiter.plot();
        VBox vBox = new VBox(potentialPlot);
        HBox hBox2 = new HBox();
        hBox2.getChildren().add(vBox);
        Scene dataScene = new Scene(hBox2, 500, 500);
        dataStage.setScene(dataScene);
        TextArea dataText = new TextArea(orbiter.getDataText());
        dataText.setEditable(false);
        String[] labels = new String[]{"M", "size", "r0", "phi0", "t0", "direction", "dphi0", "dt0", "plotMax"};
        String[] values = new String[]{"" + orbiter.getM(), "" + orbiter.getSize(),
                "" + orbiter.getR()[0], "" + orbiter.getPhi()[0], "" + orbiter.getT()[0], "" + orbiter.getDirection(),
                "" + orbiter.getDphi0(), "" + orbiter.getDt0(), "" + orbiter.getPlotMax()};


        TextField[] textFields = new TextField[9];

        Button[] buttons = new Button[9];
        for (int i = 0; i < 9; i++) {
            Button button = new Button(labels[i]);
            button.setMinSize(100, 25);
            button.setAlignment(Pos.BASELINE_LEFT);
            TextField textField = new TextField();
            HBox hBox = new HBox(button, textField);
            vBox.getChildren().add(hBox);
            textField.setText(values[i]);
            textFields[i] = textField;
            buttons[i] = button;
        }

        TableView<DataValue> table = new TableView<>();
        TableColumn labelCol = new TableColumn("Label");

        labelCol.setCellValueFactory(
                new PropertyValueFactory<>("label"));

        TableColumn dataCol = new TableColumn("Data");

        dataCol.setCellValueFactory(
                new PropertyValueFactory<>("data"));
        table.setMaxHeight(500);
        table.getColumns().addAll(labelCol, dataCol);
        table.getItems().clear();

        for(int i = 0; i < 9; i++) {
            table.getItems().add(new DataValue(buttons[i].getText(), textFields[i].getText()));
        }
        table.getItems().add(new DataValue("e", orbiter.getE() + ""));
        table.getItems().add(new DataValue("l", orbiter.getL() + ""));
        table.getItems().add(new DataValue("b", orbiter.getB() + ""));
        table.getItems().add(new DataValue("epsilon", orbiter.getEpsilon() + ""));





        hBox2.getChildren().add(table);

        Button updateButton = new Button("update");

        updateButton.setOnAction(event -> {
            orbiter.initialize(
                    Double.parseDouble(textFields[0].getText()),
                    Integer.parseInt(textFields[1].getText()),
                    Double.parseDouble(textFields[2].getText()),
                    Double.parseDouble(textFields[3].getText()),
                    Double.parseDouble(textFields[4].getText()),
                    Integer.parseInt(textFields[5].getText()),
                    Double.parseDouble(textFields[6].getText()),
                    Double.parseDouble(textFields[7].getText()),
                    Double.parseDouble(textFields[8].getText())
            );

            table.getItems().clear();
            for(int i = 0; i < 9; i++) {
                table.getItems().add(new DataValue(buttons[i].getText(), textFields[i].getText()));
            }
            table.getItems().add(new DataValue("e", orbiter.getE() + ""));
            table.getItems().add(new DataValue("l", orbiter.getL() + ""));
            table.getItems().add(new DataValue("b", orbiter.getB() + ""));
            table.getItems().add(new DataValue("epsilon", orbiter.getEpsilon() + ""));


            vBox.getChildren().remove(0);
            vBox.getChildren().add(0, orbiter.plot());
        });
        Button runButton = new Button("run");
        runButton.setOnAction(event -> {
            Orbiter newOrbiter = new Orbiter();
            newOrbiter.initialize(
                    orbiter.getM(),
                    orbiter.getSize(),
                    orbiter.getR()[0],
                    orbiter.getPhi()[0],
                    orbiter.getT()[0],
                    orbiter.getDirection(),
                    orbiter.getDphi0(),
                    orbiter.getDt0(),
                    orbiter.getPlotMax()
            );
            runOrbiter(newOrbiter);
        });

        vBox.getChildren().add(updateButton);
        vBox.getChildren().add(runButton);


        Stage simulationStage = new Stage();
        initializeSimulationStage(simulationStage, orbiter);
        Scene simulationScene = simulationStage.getScene();


        Box center = new Box(6, 6, 6);
        center.setMaterial(new PhongMaterial(Color.BLUEVIOLET));
        group.getChildren().add(center);

        dataStage.show();

    }

    private void runOrbiter(Orbiter orbiter) {

        RotationGroup newGroup = new RotationGroup();
        newGroup.getChildren().add(orbiter);

        if (randomTheta) {
            newGroup.rotateByY(Math.random() * 360);
            newGroup.rotateByX(Math.random() * 360);
        }

        group.getChildren().add(newGroup);

        orbiter.RungeKutta(step, turnCheck);
        double[] r = orbiter.getR();
        double[] phi = orbiter.getPhi();
        Color orbiterColor = Color.color(Math.random(), Math.random(), Math.random());
        PhongMaterial material = new PhongMaterial(orbiterColor);
        orbiter.setMaterial(material);
        Color tracerColor = Color.color(Math.random(), Math.random(), Math.random());

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {


            Stage analysisStage = new Stage();
            int t = 0;
            ArrayList<Node> tracers = new ArrayList<>();


            @Override
            public void run() {

                Platform.runLater(() -> {

                    if (t >= r.length || r[t] <= 0) {

                        timer.cancel();
                        group.getChildren().remove(newGroup);


                        if (toPlot) {
                            initializeAnalysisStage(analysisStage, orbiter);

                        }


                    } else {
                        if (t % 10 == 0) {
                            orbiter.translateXProperty().set(r[t] * Math.cos(phi[t]));
                            orbiter.translateYProperty().set(r[t] * Math.sin(phi[t]));
                            Sphere tracer = new Sphere(0.3);
                            tracer.translateXProperty().set(r[t] * Math.cos(phi[t]));
                            tracer.translateYProperty().set(r[t] * Math.sin(phi[t]));
                            Material tracerMaterial = new PhongMaterial(tracerColor);
                            tracer.setMaterial(tracerMaterial);


                            newGroup.getChildren().add(tracer);
                            tracers.add(tracer);


                        }

                        t++;
                    }


                });

            }
        }, 0, 1);
    }
}









