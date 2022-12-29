package orbital.entity;

import com.sun.javafx.geometry.BoundsUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import orbital.data.OrbitalData;
import orbital.data.OrbitalIntegrator;
import ui.RotationGroup;

import java.util.ArrayList;
import java.util.function.Function;

public abstract class Orbiter extends Box {

    public double dx = 0;
    public double dy = 0;
    public void generateTracer() {
        RotationGroup parent = (RotationGroup) getParent();
        if (parent != null) {
            Tracer tracer = new Tracer();
            tracer.setMaterial(new PhongMaterial(tracerColor));
            parent.getChildren().add(tracer);
            tracer.setTranslateX(getTranslateX());
            tracer.setTranslateY(getTranslateY());
        }
    }

    public Color getTracerColor() {
        return Color.color(tracerColor.getRed(), tracerColor.getGreen(), tracerColor.getBlue());
    }


    public Orbiter(Orbiter orbiter) {
        super(10, 10, 10);
        setMaterial(new PhongMaterial(orbiter.getTracerColor()));
        orbitalData = orbiter.orbitalData.clone();
        RotationGroup orbitalPlane = new RotationGroup();
        setOrbitalPlane(orbitalPlane);
        orbitalIntegrator = new OrbitalIntegrator(orbiter.getOrbitalIntegrator().getIntegrationOrder());
        tracerColor = orbiter.getTracerColor();
    }

    public void setOrbitalData(OrbitalData orbitalData) {
        this.orbitalData = orbitalData.clone();
    }

    public OrbitalIntegrator getOrbitalIntegrator() {
        return orbitalIntegrator;
    }

    protected RotationGroup orbitalPlane;

    protected OrbitalIntegrator orbitalIntegrator = new OrbitalIntegrator(1);

    protected OrbitalData orbitalData;

    protected Color tracerColor = Color.color(Math.random(), Math.random(), Math.random());


    public Orbiter(OrbitalData orbitalData) {
        super(10, 10, 10);
        setMaterial(new PhongMaterial(tracerColor));
        this.orbitalData = orbitalData.clone();
        RotationGroup orbitalPlane = new RotationGroup();
        setOrbitalPlane(orbitalPlane);
    }

    public RotationGroup getOrbitalPlane() {
        return orbitalPlane;
    }

    public void setOrbitalPlane(RotationGroup orbitalPlane) {
        if (this.orbitalPlane != null) {
            this.orbitalPlane.getChildren().remove(this);
        }
        this.orbitalPlane = orbitalPlane;
        orbitalPlane.getChildren().add(this);
        orbitalPlane.rotateByX(orbitalData.getRotationalData(OrbitalData.X_INDEX));
        orbitalPlane.rotateByY(orbitalData.getRotationalData(OrbitalData.Y_INDEX));
        orbitalPlane.rotateByZ(orbitalData.getRotationalData(OrbitalData.Z_INDEX));
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

        Point3D cartesianPoint = orbiter.getGlobalCartesianCoordinates();
        Point3D sphericalPoint = cartesianToSpherical(orbiter.globalToLocal(cartesianPoint));



        double[] equatorialData = orbiter.getOrbitalData().getEquatorialData();
        equatorialData[OrbitalData.R_INDEX] = sphericalPoint.getX();
        equatorialData[OrbitalData.THETA_INDEX] = sphericalPoint.getY();
        equatorialData[OrbitalData.PHI_INDEX] = sphericalPoint.getZ();

        double r = equatorialData[OrbitalData.R_INDEX];
        double d = orbiter.getOrbitalData().getEquatorialData(OrbitalData.DIRECTION_INDEX);

        double h = orbiter.getOrbitalData().getUtilityData(OrbitalData.STEP_SIZE_INDEX);

        double k1 = orbiter.computeGeodesicFunction();
        double k1_ = orbiter.computeGeodesicArgument(r + d * h * k1);

        if (k1_ < 0.00001) {
            d *= -1;
        }




        double e = equatorialData[OrbitalData.E_INDEX];
        double l = equatorialData[OrbitalData.L_INDEX];
        double phi = equatorialData[OrbitalData.PHI_INDEX];
        double M = orbiter.getOrbitalData().getMassData(OrbitalData.SCH_MASS_INDEX);



        equatorialData[OrbitalData.T_INDEX] += h;

        equatorialData[OrbitalData.TAU_INDEX] += h * (1 - 2 * (M / r)) / e;

        equatorialData[OrbitalData.R_INDEX] += d * h * k1;

        equatorialData[OrbitalData.THETA_INDEX] += 0;

        equatorialData[OrbitalData.PHI_INDEX] += h * ((1 - 2 * (M / r)) * l) / (Math.pow(r, 2) * e);

        double dr = d * h * k1;
        double dphi = h * ((1 - 2 * (M / r)) * l) / (Math.pow(r, 2) * e);
        double dx = dr * Math.cos(phi) - r * Math.sin(phi) * dphi;
        double dy = dr * Math.sin(phi) + r * Math.cos(phi) * dphi;


        orbiter.dx = dx;
        orbiter.dy = dy;




        equatorialData[OrbitalData.DIRECTION_INDEX] = d;

        orbiter.getOrbitalData().setEquatorialData(equatorialData);


    }

    public void translate(double dx, double dy) {
        setTranslateX(getTranslateX() + dx);
        setTranslateY(getTranslateY() + dy);
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

    public Point3D localToGlobal(Point3D localPoint) {
        Bounds localBounds = BoundsUtils.createBoundingBox(
                localPoint, localPoint, localPoint, localPoint, localPoint, localPoint, localPoint, localPoint
        );
        Bounds boundsInParent = localToParent(localBounds);
        Bounds boundsInScene = localToScene(boundsInParent);
        double x = (boundsInScene.getMaxX() + boundsInScene.getMinX()) / 2;
        double y = (boundsInScene.getMaxY() + boundsInScene.getMinY()) / 2;
        double z = (boundsInScene.getMaxZ() + boundsInScene.getMinZ()) / 2;
        return new Point3D(x, y, z);

    }

}






