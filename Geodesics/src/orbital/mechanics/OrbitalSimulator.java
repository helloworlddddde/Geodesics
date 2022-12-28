package orbital.mechanics;

import javafx.scene.Scene;
import javafx.scene.SubScene;
import orbital.entity.Orbiter;
import ui.RotationGroup;

import java.util.ArrayList;

public class OrbitalSimulator {


    private RotationGroup tempCenterPlane = new RotationGroup();
    private final SubScene tempSimulationSubScene = new SubScene(tempCenterPlane, 500, 500);
    private final ArrayList<ArrayList<double[]>> simulationData = new ArrayList<>();
    private ArrayList<Orbiter> orbiters;
    private double stepSize;
    private double turnOffset;
    private double duration;


    public OrbitalSimulator(ArrayList<Orbiter> orbiters, double stepSize, double turnOffset, double duration) {
        this.orbiters = orbiters;
        this.stepSize = stepSize;
        this.turnOffset = turnOffset;
        this.duration = duration;

        setup();
        simulate();
    }

    private void setup() {

        for(Orbiter orbiter : orbiters) {
            tempCenterPlane.getChildren().add(orbiter.getOrbitalPlane());
            simulationData.add(new ArrayList<>());
        }


    }

    private void simulate() {
        double t = 0;
        while(t < duration) {
            for(int i = 0; i < orbiters.size(); i++) {

                ArrayList<double[]> orbiterData = simulationData.get(i);
                Orbiter orbiter = orbiters.get(i);
                orbiterData.add(orbiter.getData().clone());
                orbiter.rungeKutta(stepSize, turnOffset, orbiters);
                orbiter.translate();

            }

            t += stepSize;

        }


    }

    public ArrayList<ArrayList<double[]>> getSimulationData() {
        return simulationData;
    }


}
