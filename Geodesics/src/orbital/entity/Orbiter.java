package orbital.entity;

import com.sun.javafx.geometry.BoundsUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import orbital.data.OrbitalData;
import orbital.data.OrbitalIntegrator;
import ui.RotationGroup;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class Orbiter extends Box {

    public void generateTracer() {
        RotationGroup parent = (RotationGroup) getParent();
        if (parent != null) {
            Sphere tracer = new Sphere();
            tracer.setMaterial(new PhongMaterial(tracerColor));
            parent.getChildren().add(tracer);
            tracer.setTranslateX(getTranslateX());
            tracer.setTranslateY(getTranslateY());
        }
    }

    RotationGroup orbitalPlane;

    OrbitalIntegrator orbitalIntegrator = new OrbitalIntegrator(1);

    OrbitalData orbitalData;

    Color tracerColor = Color.color(Math.random(), Math.random(), Math.random());


    public Orbiter(OrbitalData orbitalData) {
        super(10, 10, 10);

        this.orbitalData = orbitalData.clone();

        RotationGroup orbitalPlane = new RotationGroup();
        setOrbitalPlane(orbitalPlane);
    }

    public RotationGroup getOrbitalPlane() {
        return orbitalPlane;
    }

    public void setOrbitalPlane(RotationGroup orbitalPlane) {
        this.orbitalPlane = orbitalPlane;
        orbitalPlane.getChildren().add(this);
        orbitalPlane.rotateByX(orbitalData.getRotationalData(OrbitalData.X_INDEX));
        orbitalPlane.rotateByX(orbitalData.getRotationalData(OrbitalData.Y_INDEX));
        orbitalPlane.rotateByX(orbitalData.getRotationalData(OrbitalData.Z_INDEX));
    }

    public abstract double computeEffectivePotential(double r, double l);

    public abstract double computeEpsilon();

    public abstract double computeEffectivePotential();

    public abstract double computeGeodesicArgument(double r);

    public abstract double computeGeodesicArgument();

    public abstract double computeGeodesicFunction();

    public OrbitalData getOrbitalData() {
        return orbitalData;
    }

    public static Point3D cartesianToSpherical(Point3D point3D) {

        double x = point3D.getX();
        double y = point3D.getY();

        double r = Math.sqrt(x * x + y * y);
        double theta = Math.PI / 2;
        double phi = Math.atan(Math.abs(y / x));

        Function<Double, Double> adjustPhi = (p) -> {
            if (y >= 0 && x >= 0) {
                p = p;
            } else if (y >= 0 && x <= 0) {
                p = Math.PI - p;
            } else if (y <= 0 && x <= 0) {
                p = Math.PI + p;
            } else if (y <= 0 && x >= 0) {
                p = 2 * Math.PI - p;
            }
            return p;
        };

        phi = adjustPhi.apply(phi);

        return new Point3D(r, theta, phi);
    }


    public static void rungeKuttaIntegrate(Orbiter orbiter, ArrayList<Orbiter> neighbors) {

    }

    public static void eulerIntegrate(Orbiter orbiter, ArrayList<Orbiter> neighbors) {

        double[] equatorialData = orbiter.getOrbitalData().getEquatorialData();
        double r = equatorialData[OrbitalData.R_INDEX];
        double d = orbiter.getOrbitalData().getEquatorialData(OrbitalData.DIRECTION_INDEX);

        double h = orbiter.getOrbitalData().getUtilityData(OrbitalData.STEP_SIZE_INDEX);

        double k1 = orbiter.computeGeodesicFunction();
        double k1_ = orbiter.computeGeodesicArgument(r + d * h * k1);

        if (k1_ < 0.000001) {
            d *= -1;
        }




        double e = equatorialData[OrbitalData.E_INDEX];
        double l = equatorialData[OrbitalData.L_INDEX];

        double M = orbiter.getOrbitalData().getMassData(OrbitalData.SCH_MASS_INDEX);


        equatorialData[OrbitalData.T_INDEX] += h;

        equatorialData[OrbitalData.TAU_INDEX] += h * (1 - 2 * (M / r)) / e;

        equatorialData[OrbitalData.R_INDEX] += d * h * k1;

        equatorialData[OrbitalData.THETA_INDEX] += 0;

        equatorialData[OrbitalData.PHI_INDEX] += h * ((1 - 2 * (M / r)) * l) /
                (Math.pow(r, 2) * e);

        equatorialData[OrbitalData.DIRECTION_INDEX] = d;

        orbiter.getOrbitalData().setEquatorialData(equatorialData);


    }




    public void translate() {
        double r = orbitalData.getEquatorialData(OrbitalData.R_INDEX);
        double phi = orbitalData.getEquatorialData(OrbitalData.PHI_INDEX);
        setTranslateX(r * Math.cos(phi));
        setTranslateY(r * Math.sin(phi));
    }

    public Point3D getGlobalCartesianCoordinates() {
        Bounds boundsInScene = localToScene(getBoundsInLocal());
        double x = (boundsInScene.getMaxX() + boundsInScene.getMinX()) / 2;
        double y = (boundsInScene.getMaxY() + boundsInScene.getMinY()) / 2;
        double z = (boundsInScene.getMaxZ() + boundsInScene.getMinZ()) / 2;
        return new Point3D(x, y, z);
    }

    public Point3D getLocalCartesianCoordinates() {
        Bounds boundsInParent = localToParent(getBoundsInLocal());
        double x = (boundsInParent.getMaxX() + boundsInParent.getMinX()) / 2;
        double y = (boundsInParent.getMaxY() + boundsInParent.getMinY()) / 2;
        double z = (boundsInParent.getMaxZ() + boundsInParent.getMinZ()) / 2;
        return new Point3D(x, y, z);
    }

    public Point3D globalToLocal(Point3D globalPoint) {
        Bounds globalBounds = BoundsUtils.createBoundingBox(
                globalPoint, globalPoint, globalPoint, globalPoint, globalPoint, globalPoint, globalPoint, globalPoint
        );
        Bounds boundsInLocal = sceneToLocal(globalBounds);
        Bounds boundsInParent = localToParent(boundsInLocal);
        double x = (boundsInParent.getMaxX() + boundsInParent.getMinX()) / 2;
        double y = (boundsInParent.getMaxY() + boundsInParent.getMinY()) / 2;
        double z = (boundsInParent.getMaxZ() + boundsInParent.getMinZ()) / 2;
        return new Point3D(x, y, z);
    }

}






