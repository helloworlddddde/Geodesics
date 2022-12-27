package ui;

import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import orbital.mechanics.DataGenerator;

import java.util.ArrayList;

public class DataVisualizer {

    public static LineChart<Number, Number> generateChart(ArrayList<double[]>... dataSets) {

        Axis<Number> xAxis = new NumberAxis();
        Axis<Number> yAxis = new NumberAxis();
        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);

        for(ArrayList<double[]> dataSet : dataSets) {
            XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();;
            for(double[] dataPair : dataSet) {
                series.getData().add(new XYChart.Data<Number, Number>(dataPair[0], dataPair[1]));
            }
            lineChart.getData().add(series);
        }

        lineChart.setLegendVisible(false);
        lineChart.setCreateSymbols(false);
        lineChart.setMinHeight(500);
        lineChart.setMinWidth(500);

        return lineChart;
    }



}
