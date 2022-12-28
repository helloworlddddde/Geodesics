package orbital.mechanics;

import javafx.scene.SubScene;
import orbital.entity.Orbiter;
import ui.RotationGroup;

import java.util.ArrayList;

public class OrbitalSimulator {



    private RotationGroup centerPlane = new RotationGroup();
    private final SubScene simulationSubScene = new SubScene(centerPlane, 500, 500);

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

    }



    private void simulate(boolean isCoordinateTimeParameterized) {
        if (isCoordinateTimeParameterized) {
            simulateCoordinateTimeParameterization();
        } else {
            simulateProperTimeParameterization();
        }
    }

    private void simulateCoordinateTimeParameterization() {

    }

    private void simulateProperTimeParameterization() {

    }


}
