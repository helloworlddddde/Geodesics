package orbital.entity;

import java.util.function.Supplier;

public abstract class Orbiter {
    protected double mass;
    protected double schMass;
    protected double e;
    protected double l;
    protected int direction;
    protected double[] data;
    protected double[] rotationalOffsets;
    protected boolean collided;


    public Orbiter(Orbiter orbiter) {
        this.mass = orbiter.mass;
        this.schMass = orbiter.schMass;
        this.e = orbiter.e;
        this.l = orbiter.l;
        this.direction = orbiter.direction;
        this.data = orbiter.data.clone();
        this.rotationalOffsets = orbiter.rotationalOffsets.clone();
        this.collided = orbiter.collided;
    }

    public Orbiter(double mass, double schMass, double e, double l, int direction) {
        this.mass = mass;
        this.schMass = schMass;
        this.e = e;
        this.l = l;
        this.direction = direction;
        data = new double[4];
        collided = false;
    }

    public void setInitialConditions(double[] data, double[] rotationalOffsets) {
        this.data = data;
        this.rotationalOffsets = rotationalOffsets;
    }

    public double[] getData() {
        return data;
    }

    public boolean isCollided() {
        return collided;
    }

    public double getE() {
        return e;
    }

    public double getL() {
        return l;
    }

    public abstract double effectivePotential(double radius);

    public abstract double geodesicFunction(double radius);

    public void rungeKutta(double stepSize, double turnOffset) {

        double k1 = geodesicFunction(data[1]);
        double k2 = geodesicFunction(data[1] + stepSize * k1 / 2);
        double k3 = geodesicFunction(data[1] + stepSize * k2 / 2);
        double k4 = geodesicFunction(data[1] + stepSize * k3);


        Supplier<Boolean> isTurningPoint = () ->
                Double.isNaN(geodesicFunction(data[1] + (1.0 / turnOffset)
                        * stepSize * direction * (k1 + 2 * k2 + 2 * k3 + k4)));

        Supplier<Boolean> isCollision = () ->
                data[1] + (1.0 / turnOffset)
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


        data[3] = data[3] + stepSize * l / Math.pow(data[1], 2);
        data[0] = data[0] + stepSize * e / (1 - 2 * schMass / data[1]);
        data[1] = data[1] + (1.0 / 6) * stepSize * direction * (k1 + 2 * k2 + 2 * k3 + k4);
        data[2] = 0;



    }


}






