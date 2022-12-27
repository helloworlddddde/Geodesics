package orbital.mechanics;

import javafx.animation.Interpolator;
import orbital.entity.Orbiter;

import java.util.ArrayList;

public class DataGenerator {

    public static ArrayList<double[]> generateGeodesicData(int dataSize, double stepSize, double turnOffset, Orbiter orbiter) {
        ArrayList<double[]> dataSet = new ArrayList<>();
        for(int i = 0; i < dataSize && !orbiter.isCollided(); i++) {
            double[] data = orbiter.getData();
            dataSet.add(data);
            orbiter.rungeKutta(stepSize, turnOffset);
        }
        return dataSet;
    }



    public static double interpolate(double target, double[] prevData, double[] currData) {
        Interpolator linear = Interpolator.LINEAR;
        return 0;
    }

    public static ArrayList<double[]> generatePotentialData(double minRadius, double maxRadius, double stepSize, Orbiter orbiter) {
        ArrayList<double[]> dataSet = new ArrayList<>();
        double currentRadius = minRadius;
        while(currentRadius <= maxRadius) {
            double[] data = new double[]{currentRadius, orbiter.effectivePotential(currentRadius)};
            dataSet.add(data);
            currentRadius += stepSize;
        }
        return dataSet;
    }

    public static ArrayList<double[]> generateEpsilonData(double minRadius, double maxRadius, double stepSize, Orbiter orbiter) {
        ArrayList<double[]> dataSet = new ArrayList<>();
        double currentRadius = minRadius;
        while(currentRadius <= maxRadius) {
            double[] data = new double[]{currentRadius, (Math.pow(orbiter.getE(), 2) - 1) / 2};
            dataSet.add(data);
            currentRadius += stepSize;
        }
        return dataSet;
    }




}
