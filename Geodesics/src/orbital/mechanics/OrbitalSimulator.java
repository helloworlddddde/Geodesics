package orbital.mechanics;

import javafx.animation.Interpolator;
import javafx.scene.SubScene;
import orbital.entity.Orbiter;
import ui.RotationGroup;

import java.util.ArrayList;

public class OrbitalSimulator {



    private RotationGroup tempCenterPlane = new RotationGroup();
    private final SubScene tempSimulationSubScene = new SubScene(tempCenterPlane, 500, 500);
    private final ArrayList<ArrayList<double[]>> simulationData = new ArrayList<>();
    private ArrayList<Orbiter> orbiters;
    private double globalStepSize;
    private double duration;



    public OrbitalSimulator(boolean isCoordinateTimeParameterized, ArrayList<Orbiter> orbiters, double globalStepSize, double duration) {
        this.orbiters = orbiters;
        this.globalStepSize = globalStepSize;
        this.duration = duration;

        setup();
        simulate(isCoordinateTimeParameterized);
    }

    private void setup() {

        for(Orbiter orbiter : orbiters) {
            tempCenterPlane.getChildren().add(orbiter.getOrbitalPlane());
            simulationData.add(new ArrayList<double[]>() {{
                add(orbiter.getData());
            }});
        }
    }



    private void simulate(boolean isCoordinateTimeParameterized) {
        if (isCoordinateTimeParameterized) {
            simulateCoordinateTimeParameterization();
        } else {
            simulateProperTimeParameterization();
        }
    }

    private void simulateCoordinateTimeParameterization() {
        double t = 1 * globalStepSize;
        while(t < duration) {
            for(int i = 0; i < orbiters.size(); i++) {

                ArrayList<double[]> orbiterData = simulationData.get(i);
                Orbiter orbiter = orbiters.get(i);

                double[] prev = orbiterData.get(orbiterData.size() - 1);
                double[] next = orbiter.getData().clone();


                while(orbiter.getData()[0] < t) {
                    prev = orbiter.getData().clone();
                    orbiter.orbitalIntegrate(orbiters);
                    next = orbiter.getData().clone();
                }

                Interpolator linearInterpolator = Interpolator.LINEAR;
                double fraction = (t - prev[0]) / (next[0] - prev[0]);
                double intR = linearInterpolator.interpolate(prev[1], next[1], fraction);
                double intPhi = linearInterpolator.interpolate(prev[3], next[3], fraction);
                double intTau = linearInterpolator.interpolate(prev[4], next[4], fraction);
                orbiter.setData(new double[]{t, intR, Math.PI/2, intPhi, intTau});
                orbiterData.add(orbiter.getData().clone());
            }

            for(Orbiter orbiter : orbiters) {
                orbiter.translate();
            }
            t += globalStepSize;
        }
    }

    private void simulateProperTimeParameterization() {
        double t = 1 * globalStepSize;
        while(t < duration) {
            for(int i = 0; i < orbiters.size(); i++) {
                ArrayList<double[]> orbiterData = simulationData.get(i);
                Orbiter orbiter = orbiters.get(i);
                orbiterData.add(orbiter.getData().clone());
                orbiter.orbitalIntegrate(orbiters);
                orbiter.translate();
            }

            t += globalStepSize;

        }


    }

    public ArrayList<ArrayList<double[]>> getSimulationData() {
        return simulationData;
    }


}
