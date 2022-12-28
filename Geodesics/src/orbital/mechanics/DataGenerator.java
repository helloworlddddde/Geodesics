package orbital.mechanics;

import orbital.entity.Orbiter;

import java.util.ArrayList;

public class DataGenerator {


    public static ArrayList<double[]> generateEffectivePotentialData(double minRadius, double maxRadius, double stepSize, Orbiter orbiter) {
        ArrayList<double[]> dataSet = new ArrayList<>();
        double currentRadius = minRadius;
        while(currentRadius <= maxRadius + stepSize) {
            double[] data = new double[]{currentRadius, orbiter.effectivePotential(currentRadius)};
            dataSet.add(data);
            currentRadius += stepSize;
        }
        return dataSet;
    }

    public static ArrayList<double[]> generateEpsilonData(double minRadius, double maxRadius, double stepSize, Orbiter orbiter) {
        ArrayList<double[]> dataSet = new ArrayList<>();
        double currentRadius = minRadius;
        while(currentRadius <= maxRadius + stepSize) {
            double[] data = new double[]{currentRadius, (Math.pow(orbiter.getE(), 2) - 1) / 2};
            dataSet.add(data);
            currentRadius += stepSize;
        }
        return dataSet;
    }




}
