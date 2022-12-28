package orbital.data;


import orbital.entity.Orbiter;

import java.util.ArrayList;

public class OrbitalIntegrator {

    private interface OrbitalIntegratorFunction {
        void orbitalIntegrate(Orbiter orbiter, ArrayList<Orbiter> neighbors);
    }

    int integrationOrder;
    OrbitalIntegratorFunction orbitalIntegratorFunction;

    public OrbitalIntegrator(int integrationOrder) {
        this.integrationOrder = integrationOrder;

        switch(integrationOrder) {
            case 1:
                orbitalIntegratorFunction = Orbiter::eulerIntegrate;
                break;
            case 4:
                orbitalIntegratorFunction = Orbiter::rungeKuttaIntegrate;
                break;
        }

    }

    public void orbitalIntegrate(Orbiter orbiter, ArrayList<Orbiter> neighbors) {
        orbitalIntegratorFunction.orbitalIntegrate(orbiter, neighbors);
    }


}
