package orbital.mechanics;

import javafx.geometry.Point3D;
import javafx.scene.SubScene;
import orbital.data.OrbitalData;
import orbital.entity.Orbiter;
import ui.RotationGroup;

import java.util.ArrayList;

public class OrbitalSimulator {



    private RotationGroup centerPlane = new RotationGroup();
    private final SubScene simulationSubScene = new SubScene(centerPlane, 500, 500);

    private final ArrayList<ArrayList<OrbitalData>> simulationData = new ArrayList<>();
    private final ArrayList<ArrayList<Point3D>> coordinatesData = new ArrayList<>();
    private ArrayList<Orbiter> orbiters;

    private double globalStepSize;
    private double duration;



    public OrbitalSimulator(ArrayList<Orbiter> orbiters, double globalStepSize, double duration) {
        this.orbiters = orbiters;
        this.globalStepSize = globalStepSize;
        this.duration = duration;
        setup();
        simulate();
    }

    private void setup() {
        for(Orbiter o : orbiters) {
            centerPlane.getChildren().add(o.getOrbitalPlane());
            simulationData.add(new ArrayList<>());
            coordinatesData.add(new ArrayList<>());
            o.getOrbitalData().setUtilityData(OrbitalData.STEP_SIZE_INDEX, globalStepSize);
            o.translate();
        }
    }

    private void simulate() {
        double t = 0;
        while (t < duration + globalStepSize) {
            for(int i = 0; i < orbiters.size(); i++) {
                ArrayList<OrbitalData> orbiterData = simulationData.get(i);
                Orbiter orbiter = orbiters.get(i);
                orbiterData.add(orbiter.getOrbitalData().clone());
                orbiter.getOrbitalIntegrator().orbitalIntegrate(orbiter, orbiters);
                coordinatesData.get(i).add(orbiter.getGlobalCartesianCoordinates());
            }

            for(int i = 0; i < orbiters.size(); i++) {
                Orbiter o = orbiters.get(i);
                o.translate(o.dx, o.dy);
            }

            t += globalStepSize;
        }

    }

    public ArrayList<ArrayList<Point3D>> getCoordinatesData() {
        return coordinatesData;
    }
    public ArrayList<ArrayList<OrbitalData>> getSimulationData() {
        return simulationData;
    }


}
