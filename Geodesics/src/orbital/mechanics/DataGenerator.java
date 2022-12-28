package orbital.mechanics;

import javafx.scene.chart.XYChart;
import orbital.data.OrbitalData;
import orbital.entity.Orbiter;

import java.util.ArrayList;

public class DataGenerator {


    public static ArrayList<XYChart.Data<Number, Number>> generateEffectivePotentialData(double minR, double maxR, double stepSize, Orbiter orbiter) {

        ArrayList<XYChart.Data<Number, Number>> dataSet = new ArrayList<>();
        double r = minR;
        double l = orbiter.getOrbitalData().getEquatorialData(OrbitalData.L_INDEX);

        while(r <= maxR + stepSize) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(r, orbiter.computeEffectivePotential(r, l));
            dataSet.add(data);
            r += stepSize;
        }

        return dataSet;
    }

    public static ArrayList<XYChart.Data<Number, Number>> generateEpsilonData(double minR, double maxR, double stepSize, Orbiter orbiter) {

        ArrayList<XYChart.Data<Number, Number>> dataSet = new ArrayList<>();
        double r = minR;
        double e = orbiter.getOrbitalData().getEquatorialData(OrbitalData.E_INDEX);

        while(r <= maxR + stepSize) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(r, orbiter.computeEpsilon());
            dataSet.add(data);
            r += stepSize;
        }

        return dataSet;

    }




}
