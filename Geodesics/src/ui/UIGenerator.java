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
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import orbital.entity.Orbiter;
import orbital.entity.Particle;
import orbital.mechanics.DataGenerator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

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
        runButton.setOnAction(event -> {
            Orbiter newOrbiter;
            if (orbiter instanceof Particle) {
                newOrbiter = new Particle(orbiter);
            } else {
                newOrbiter = new Particle(orbiter);
            }
            runOrbiter(newOrbiter, simulationSubScene, orbiterTrackerSubScene, 1.0, 3.0);
        });
        HBox rootHorizontalBox = new HBox(effectivePotentialSubScene,
                simulationSubScene,  orbiterTrackerSubScene, runButton);

        Scene scene = new Scene(rootHorizontalBox, 1400, 800, true);
        setMainSceneControls(scene, effectivePotentialSubScene, simulationSubScene);
        return scene;
    }

    public static class PointView3D {
        private final SimpleStringProperty label;
        private final SimpleStringProperty x;
        private final SimpleStringProperty y;
        private final SimpleStringProperty z;
        private final SimpleStringProperty tau;

        private PointView3D(String label, String x, String y, String z, String tau) {
            this.label = new SimpleStringProperty(label);
            this.x = new SimpleStringProperty(x);
            this.y = new SimpleStringProperty(y);
            this.z = new SimpleStringProperty(z);
            this.tau = new SimpleStringProperty(tau);
        }

        private PointView3D(String label, Point3D point3D, String tau) {
            this.label = new SimpleStringProperty(label);
            this.x = new SimpleStringProperty(Double.toString(point3D.getX()));
            this.y = new SimpleStringProperty(Double.toString(point3D.getY()));
            this.z = new SimpleStringProperty(Double.toString(point3D.getZ()));
            this.tau = new SimpleStringProperty(tau);
        }

        public String getLabel() {
            return label.get();
        }

        public SimpleStringProperty labelProperty() {
            return label;
        }

        public void setLabel(String label) {
            this.label.set(label);
        }

        public String getX() {
            return x.get();
        }

        public SimpleStringProperty xProperty() {
            return x;
        }

        public void setX(String x) {
            this.x.set(x);
        }

        public String getY() {
            return y.get();
        }

        public String getTau() {
            return tau.get();
        }

        public SimpleStringProperty tauProperty() {
            return tau;
        }

        public void setTau(String tau) {
            this.tau.set(tau);
        }

        public SimpleStringProperty yProperty() {
            return y;
        }

        public void setY(String y) {
            this.y.set(y);
        }

        public void setPointLabels(Point3D point3D) {
            setX(Double.toString(point3D.getX()));
            setY(Double.toString(point3D.getY()));
            setZ(Double.toString(point3D.getZ()));
        }

        public String getZ() {
            return z.get();
        }

        public SimpleStringProperty zProperty() {
            return z;
        }

        public void setZ(String z) {
            this.z.set(z);
        }
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

    public static Point3D getVisualCartesianCoordinates(Shape3D orbiterShape) {
        Bounds boundsInScene = orbiterShape.localToScene(orbiterShape.getBoundsInLocal());
        double x = (boundsInScene.getMaxX() + boundsInScene.getMinX())/2;
        double y = (boundsInScene.getMaxY() + boundsInScene.getMinY())/2;
        double z = (boundsInScene.getMaxZ() + boundsInScene.getMinZ())/2;
        return new Point3D(x, y, z);
    }


    public static void runOrbiter(Orbiter newOrbiter, SubScene simulationSubScene,
                                  SubScene orbiterTrackerSubScene, double stepSize, double turnOffset) {
        Color orbiterBoxColor = Color.color(Math.random(), Math.random(), Math.random());
        Box orbiterBox = new Box(10, 10, 10);
        Material orbiterBoxMaterial = new PhongMaterial(orbiterBoxColor);
        orbiterBox.setMaterial(orbiterBoxMaterial);
        Color tracerColor = Color.color(Math.random(), Math.random(), Math.random());
        RotationGroup centerGroup = (RotationGroup) simulationSubScene.getRoot();
        RotationGroup newGroup = new RotationGroup();
        newGroup.rotateByX(360 * Math.random());
        newGroup.getChildren().add(orbiterBox);
        centerGroup.getChildren().add(newGroup);
        TableView<PointView3D> tableView = (TableView<PointView3D>) orbiterTrackerSubScene.getRoot();
        Point3D point3D = getVisualCartesianCoordinates(orbiterBox);
        PointView3D orbiterPointView3D = new PointView3D(
                Double.toString(orbiterBoxColor.getHue()), point3D, Double.toString(0));
        tableView.getItems().add(orbiterPointView3D);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            ArrayList<double[]> storedOrbiterData = new ArrayList<>();
            int t = 0;
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (t == 20000) {
                        timer.cancel();
                        centerGroup.getChildren().remove(newGroup);
                        tableView.getItems().remove(orbiterPointView3D);
                    }
                    if (t % 10 == 0) {
                        boolean found = false;
                        for(int i = 0; i < storedOrbiterData.size() - 1; i++) {
                            double[] prevOrbiterData = storedOrbiterData.get(i);
                            double[] currOrbiterData = storedOrbiterData.get(i+1);
                            if (prevOrbiterData[0] >= t && currOrbiterData[0] <= t) {
                                found = true;
                            }
                        }
                        if (found) {
                            double r = newOrbiter.getData()[1];
                            double phi = newOrbiter.getData()[3];
                            double time = newOrbiter.getData()[0];
                            orbiterBox.setTranslateX(r * Math.cos(phi));
                            orbiterBox.setTranslateY(r * Math.sin(phi));
                            Sphere tracer = new Sphere(1);
                            tracer.setTranslateX(r * Math.cos(phi));
                            tracer.setTranslateY(r * Math.sin(phi));
                            Material tracerMaterial = new PhongMaterial(tracerColor);
                            tracer.setMaterial(tracerMaterial);
                            newGroup.getChildren().add(tracer);
                            orbiterPointView3D.setPointLabels(getVisualCartesianCoordinates(orbiterBox));
                            orbiterPointView3D.setTau(Double.toString(time));
                        }

                    }
                    storedOrbiterData.add(newOrbiter.getData());
                    newOrbiter.rungeKutta(stepSize, turnOffset);
                    t++;
                });
            }
        }, 0, 1);
    }


}
