package orbital.entity;

import orbital.data.OrbitalData;

public class Particle extends Orbiter {

    public Particle(OrbitalData orbitalData) {
        super(orbitalData);
    }

    public Particle(Orbiter orbiter) {
        super(orbiter);
    }

    @Override
    public double computeEffectivePotential(double r, double l) {
        double M = orbitalData.getMassData(OrbitalData.SCH_MASS_INDEX);
        return -M / r + Math.pow(l, 2) / (2 * Math.pow(r, 2)) - M * Math.pow(l, 2) / Math.pow(r, 3);
    }

    @Override
    public double computeEpsilon() {
        double e = orbitalData.getEquatorialData(OrbitalData.E_INDEX);
        return (Math.pow(e, 2) - 1)/2;
    }

    @Override
    public double computeEffectivePotential() {
        double M = orbitalData.getMassData(OrbitalData.SCH_MASS_INDEX);
        double r = orbitalData.getEquatorialData(OrbitalData.R_INDEX);
        double l = orbitalData.getEquatorialData(OrbitalData.L_INDEX);
        return -M / r + Math.pow(l, 2) / (2 * Math.pow(r, 2)) - M * Math.pow(l, 2) / Math.pow(r, 3);
    }

    @Override
    public double computeGeodesicArgument(double r) {
        double e = orbitalData.getEquatorialData(OrbitalData.E_INDEX);
        double l = orbitalData.getEquatorialData(OrbitalData.L_INDEX);
        return computeEpsilon() - computeEffectivePotential(r, l);
    }

    @Override
    public double computeGeodesicArgument() {
        return computeEpsilon() - computeEffectivePotential();
    }

    @Override
    public double computeGeodesicFunction() {
        double M = orbitalData.getMassData(OrbitalData.SCH_MASS_INDEX);
        double r = orbitalData.getEquatorialData(OrbitalData.R_INDEX);
        double e = orbitalData.getEquatorialData(OrbitalData.E_INDEX);
        double l = orbitalData.getEquatorialData(OrbitalData.L_INDEX);
        return (1 - 2 * (M/r)) * (Math.sqrt(2 * (computeEpsilon() - computeEffectivePotential()))) / e;
    }
}
