package orbital.mechanics;

import javafx.animation.Interpolator;
import orbital.entity.Orbiter;

import java.util.ArrayList;

public class DataGenerator {

    public static ArrayList<double[]> generateGeodesicData(int dataSize, double stepSize, double turnOffset, Orbiter orbiter) {
        ArrayList<double[]> dataSet = new ArrayList<>();
        for(int i = 0; i < dataSize && !orbiter.isCollided(); i++) {
            double[] data = orbiter.getData().clone();
            dataSet.add(data);
            orbiter.rungeKutta(stepSize, turnOffset);
        }
        return dataSet;
    }



    public static double[] interpolate(double target, double[] prevData, double[] currData) {
        Interpolator linearInterpolator = Interpolator.LINEAR;
        double fraction = (target - prevData[0]) / (currData[0] - prevData[0]);
//        System.out.println("target" + target);
//        System.out.println("prevData[0]" + prevData[0]);
//        System.out.println("currData[0]" + currData[0]);
        double interpolatedR = linearInterpolator.interpolate(prevData[1], currData[1], fraction);
        double interpolatedPhi = linearInterpolator.interpolate(prevData[3], currData[3], fraction);
        double interpolatedTheta = linearInterpolator.interpolate(prevData[2], currData[2], fraction);
        return new double[]{target, interpolatedR, interpolatedTheta, interpolatedPhi};
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
