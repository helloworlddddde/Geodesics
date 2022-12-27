package ui;

import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
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


public class Main extends Application {


    public static void main(String args[]) throws Exception {
        launch(args);
    }

    // 0.08718378
    //test.initialize(1, 10000, 20, 1.3, 0, -1, 0.01075, 1.08);
    @Override
    public void start(Stage primaryStage) throws Exception {

        Orbiter orbiter = new Particle(1, 1, 0.993, 4.1, -1);

        orbiter.setInitialConditions(new double[]{0, 100, 0, 0}, new double[]{0, 0});


        primaryStage.setScene(UIGenerator.generateMainScene(orbiter));

        primaryStage.show();

    }

}








