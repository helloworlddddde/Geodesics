package orbital.entity;

public class Particle extends Orbiter {


    public Particle(double mass, double schMass, double e, double l, int direction) {
        super(mass, schMass, e, l, direction);
    }

    public Particle(Orbiter orbiter) {
        super(orbiter);
    }

    @Override
    public double effectivePotential(double radius) {
        return -schMass / radius + Math.pow(l, 2) / (2 * Math.pow(radius, 2)) - schMass * Math.pow(l, 2) / Math.pow(radius, 3);
    }

    @Override
    public double geodesicArgument(double radius) {
        return (Math.pow(e, 2) - 1) / 2 - effectivePotential(radius);
    }

    @Override
    public double geodesicFunction(double radius) {
        return Math.sqrt(2 * ((Math.pow(e, 2) - 1) / 2 - effectivePotential(radius)));
    }



}
