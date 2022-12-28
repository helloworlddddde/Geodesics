package orbital.entity;

import com.sun.javafx.geometry.BoundsUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import ui.PointView3D;
import ui.RotationGroup;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class Orbiter extends Box {

    private interface OrbitalIntegrator {
        void orbitalIntegrate(ArrayList<Orbiter> neighbors);
    }



    protected OrbitalIntegrator orbitalIntegrator = this::rungeKuttaIntegrate;
    protected int integratorOrder = 4;
    protected RotationGroup orbitalPlane = new RotationGroup();
    protected Material tracerMaterial = new PhongMaterial(Color.color(Math.random(), Math.random(), Math.random()));
    protected PointView3D globalPointView3D;
    protected PointView3D localPointView3D;
    protected double mass;
    protected double schMass;
    protected double e;
    protected double l;
    protected int direction;
    protected double[] data;
    protected double[] rotationalOffsets;
    protected boolean collided;
    protected double stepSize = 0.2;

    public void orbitalIntegrate(ArrayList<Orbiter> neighbors) {
        orbitalIntegrator.orbitalIntegrate(neighbors);
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    public void setOrbitalIntegrator(int k) {
        switch(k) {
            case 1:
                orbitalIntegrator = this::eulerIntegrate;
                integratorOrder = 1;
                break;
            case 4:
                orbitalIntegrator = this::rungeKuttaIntegrate;
                integratorOrder = 4;
                break;
        }
    }

    public int getIntegratorOrder() {
        return integratorOrder;
    }

    public double getStepSize() {
        return stepSize;
    }

    public Orbiter(Orbiter orbiter) {
        super(10, 10, 10);
        setMaterial(new PhongMaterial(Color.color(Math.random(), Math.random(), Math.random())));

        this.mass = orbiter.mass;
        this.schMass = orbiter.schMass;
        this.e = orbiter.e;
        this.l = orbiter.l;
        this.direction = orbiter.direction;
        this.data = orbiter.data.clone();
        this.rotationalOffsets = orbiter.rotationalOffsets.clone();
        this.collided = orbiter.collided;
        orbitalPlane = new RotationGroup();
        orbitalPlane.rotateByX(rotationalOffsets[0]);
        orbitalPlane.rotateByY(rotationalOffsets[1]);
        orbitalPlane.rotateByZ(rotationalOffsets[2]);
        orbitalPlane.getChildren().add(this);
        translate();
    }

    public Orbiter(double mass, double schMass, double e, double l, int direction) {
        super(10, 10, 10);
        setMaterial(new PhongMaterial(Color.color(Math.random(), Math.random(), Math.random())));
        orbitalPlane.getChildren().add(this);
        this.mass = mass;
        this.schMass = schMass;
        this.e = e;
        this.l = l;
        this.direction = direction;
        data = new double[5];
        rotationalOffsets = new double[3];
        collided = false;
    }

    public static Point3D toSpherical(Point3D point3D) {
        double x = point3D.getX();
        double y = point3D.getY();

        double r = Math.sqrt(x * x + y * y);
        double theta = Math.PI / 2;
        double phi = Math.atan(Math.abs(y / x));

        if (y >= 0 && x >= 0) {
            phi = phi;
        } else if (y >= 0 && x <= 0) {
            phi = Math.PI - phi;
        } else if (y <= 0 && x <= 0) {
            phi = Math.PI + phi;
        } else if (y <= 0 && x >= 0) {
            phi = 2 * Math.PI - phi;
        }

        return new Point3D(r, theta, phi);
    }

    public void setInitialConditions(double[] data, double[] rotationalOffsets) {
        this.data = data;
        this.rotationalOffsets = rotationalOffsets;
        orbitalPlane.rotateByX(rotationalOffsets[0]);
        orbitalPlane.rotateByY(rotationalOffsets[1]);
        orbitalPlane.rotateByZ(rotationalOffsets[2]);
        translate();
    }

    @Override
    public String toString() {
        return "[Orbiter: " +
                mass + ", " +
                schMass + ", " +
                e + ", " +
                l + ", " +
                direction + ", " +
                data[1] + ", " +
                data[3] + ", " +
                rotationalOffsets[0] + ", " +
                rotationalOffsets[1] + ", " +
                rotationalOffsets[2] + ", " +
                stepSize + ", " +
                integratorOrder + "]";
    }

    public double getMinimumE() {
        return Math.sqrt(2*effectivePotential(data[1])+1);
    }
    public double[] getData() {
        return data.clone();
    }

    public void setData(double[] data) {
        this.data = data.clone();
    }

    public boolean isCollided() {
        return collided;
    }

    public void setCollided(boolean collided) {
        this.collided = collided;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public RotationGroup getOrbitalPlane() {
        return orbitalPlane;
    }

    public void setOrbitalPlane(RotationGroup orbitalPlane) {
        this.orbitalPlane = orbitalPlane;
        orbitalPlane.getChildren().add(this);
    }

    public abstract double effectivePotential(double radius);

    public abstract double geodesicArgument(double radius);
    public abstract double geodesicFunction(double radius);

    public void rungeKuttaIntegrate(ArrayList<Orbiter> neighbors) {

        double k1 = geodesicFunction(data[1]);
        double k2 = geodesicFunction(data[1] + stepSize * k1 / 2);
        double k3 = geodesicFunction(data[1] + stepSize * k2 / 2);
        double k4 = geodesicFunction(data[1] + stepSize * k3);

        Supplier<Boolean> isTurningPoint = () ->
                geodesicArgument(data[1] + (1.0/6.0) * stepSize * direction * (k1 + 2 * k2 + 2 * k3 + k4)) < 0.000001;

        Supplier<Boolean> isCollision = () ->
                data[1] + (1.0 / 6.0)
                        * stepSize * direction * (k1 + 2 * k2 + 2 * k3 + k4) < 0;



        if (isTurningPoint.get()) {
            if (isCollision.get()) {
                collided = true;
            }

            direction *= -1;
        }

        if (isCollision.get()) {
            collided = true;
        }

        data[3] += stepSize * l / Math.pow(data[1], 2);
        data[0] += stepSize * e / (1 - 2 * schMass / data[1]);
        data[1] += (1.0 / 6) * stepSize * direction * (k1 + 2 * k2 + 2 * k3 + k4);
        data[2] = Math.PI / 2;
        data[4] += stepSize;

        for (Orbiter neighbor : neighbors) {
            if (neighbor != this) {
                Point3D globalPoint1 = getGlobalCartesianCoordinates();
                Point3D globalPoint2 = neighbor.getGlobalCartesianCoordinates();
                double distance = globalPoint1.distance(globalPoint2);
            }
        }


    }

    public void eulerIntegrate(ArrayList<Orbiter> neighbors) {
        double k1 = geodesicFunction(data[1]);

        Supplier<Boolean> isTurningPoint = () ->
                geodesicArgument(data[1] + stepSize * direction * k1) < 0.000001;


        Supplier<Boolean> isCollision = () ->
                data[1] + stepSize * direction * k1 < 0;

        if (isTurningPoint.get()) {
            if (isCollision.get()) {
                collided = true;
            }

            direction *= -1;
        }

        if (isCollision.get()) {
            collided = true;
        }
        data[3] += stepSize * l / Math.pow(data[1], 2);
        data[0] += stepSize * e / (1 - 2 * schMass / data[1]);
        data[1] += stepSize * direction * k1;
        data[2] = Math.PI / 2;
        data[4] += stepSize;

    }

    public void translate() {
        setTranslateX(data[1] * Math.cos(data[3]));
        setTranslateY(data[1] * Math.sin(data[3]));
        globalPointView3D = new PointView3D(getGlobalCartesianCoordinates(), Double.toString(data[4]));
        localPointView3D = new PointView3D(getLocalCartesianCoordinates(), Double.toString(data[4]));
        generateTracer();

        Point3D cartesianPoint = getGlobalCartesianCoordinates();
        cartesianPoint = globalToLocal(cartesianPoint);
        Point3D sphericalPoint = toSpherical(cartesianPoint);

        data[1] = sphericalPoint.getX();
        data[2] = sphericalPoint.getY();
        data[3] = sphericalPoint.getZ();

    }

    public PointView3D getGlobalPointView3D() {
        return globalPointView3D;
    }

    public void setGlobalPointView3D(PointView3D globalPointView3D) {
        this.globalPointView3D = globalPointView3D;
    }

    public PointView3D getLocalPointView3D() {
        return localPointView3D;
    }

    public void setLocalPointView3D(PointView3D localPointView3D) {
        this.localPointView3D = localPointView3D;
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

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getSchMass() {
        return schMass;
    }

    public void setSchMass(double schMass) {
        this.schMass = schMass;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double[] getRotationalOffsets() {
        return rotationalOffsets;
    }

    public void setRotationalOffsets(double[] rotationalOffsets) {
        this.rotationalOffsets = rotationalOffsets;
    }

    public void setTracerMaterial(Material tracerMaterial) {
        this.tracerMaterial = tracerMaterial;
    }

    public void generateTracer() {
        RotationGroup parent = (RotationGroup) getParent();
        if (parent != null) {
            Sphere tracer = new Sphere(1);
            tracer.setMaterial(tracerMaterial);
            parent.getChildren().add(tracer);
            tracer.setTranslateX(getTranslateX());
            tracer.setTranslateY(getTranslateY());
        }

    }
}






