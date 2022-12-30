package orbital.mechanics;

import javafx.geometry.Point3D;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
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

                coordinatesData.get(i).add(orbiter.getGlobalCartesianCoordinates());

                orbiterData.add(orbiter.getOrbitalData().clone());
                orbiter.getOrbitalIntegrator().orbitalIntegrate(orbiter, orbiters);

            }

            for (Orbiter o : orbiters) {
                //                Point3D v1 = o.getGlobalCartesianCoordinates();
                o.translate();
//                v1 = o.getGlobalCartesianCoordinates().subtract(v1).normalize();
//                Point3D v2 = o.getGlobalCartesianCoordinates().normalize();
//                Point3D n = v1.crossProduct(v2.normalize());
//                Point3D rotationAxis = n.crossProduct(Rotate.Z_AXIS).normalize();
//                double theta = Math.acos(n.getZ() / n.magnitude());
//                RotationGroup testGroup = new RotationGroup();
//                double roll = Math.atan2(-rotationAxis.getY() * Math.cos(theta), n.getX() * Math.sin(theta));
//                double pitch = Math.acos(Math.cos(theta));
//                double yaw = -Math.atan2(n.getY() * Math.sin(theta), n.getX() * -Math.sin(theta));
///               System.out.println(n);
//                System.out.println(n.angle(Rotate.X_AXIS));
//                System.out.println(n.angle(Rotate.Y_AXIS));
//                System.out.println(n.angle(Rotate.Z_AXIS));
//                double X = n.angle(Rotate.X_AXIS);
//                double Y = n.angle(Rotate.Y_AXIS);
//                double Z = n.angle(Rotate.Z_AXIS);
//                double[] rotationalData = new double[]{X, Y, Z};
//                o.getOrbitalData().setRotationalData(rotationalData);
//                o.setOrbitalPlane(testGroup);
//                centerPlane.getChildren().set(i, testGroup);
//                o.setTranslateZ(o.getTranslateZ() + 0.3);


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
