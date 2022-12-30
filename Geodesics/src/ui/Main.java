package ui;

import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import orbital.data.OrbitalData;
import orbital.data.OrbitalIntegrator;
import orbital.mechanics.DataGenerator;
import orbital.entity.Orbiter;
import orbital.entity.Particle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;


public class Main extends Application {


    public static void main(String args[]) throws Exception {
        launch(args);
    }

    // 0.08718378
    //test.initialize(1, 10000, 20, 1.3, 0, -1, 0.01075, 1.08);
    @Override
    public void start(Stage primaryStage) throws Exception {
        OrbitalData orbitalData = new OrbitalData(
//                new double[]{1, 1},
//                new double[]{0, 0, 100, Math.PI/2, 0, -1, 0.993, 4.1},
//                new double[]{0, 0, 0},
//                new double[]{0.2}
                new double[]{1, 1},
                new double[]{0, 0, 30, Math.PI/2, 0, -1, 0.983, 4.1},
                new double[]{0, 0, 0},
                new double[]{0.2}
        );

//        new double[]{1, 1},
//                new double[]{0, 0, 30, Math.PI/2, 0, -1, 0.983, 4.1},
//                new double[]{0, 0, 0},
//                new double[]{0.2}

        Orbiter orbiter = new Particle(orbitalData);

        primaryStage.setScene(UIGenerator.generateMainScene(orbiter));
        primaryStage.show();

    }

}








