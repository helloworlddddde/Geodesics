package orbital.data;

import java.util.Arrays;

public class OrbitalData {

    public static final int MASS_INDEX = 0;
    public static final int SCH_MASS_INDEX = 1;

    public static final int TAU_INDEX = 0;
    public static final int T_INDEX = 1;
    public static final int R_INDEX = 2;
    public static final int THETA_INDEX = 3;
    public static final int PHI_INDEX = 4;
    public static final int DIRECTION_INDEX = 5;
    public static final int E_INDEX = 6;
    public static final int L_INDEX = 7;

    public static final int X_INDEX = 0;
    public static final int Y_INDEX = 1;
    public static final int Z_INDEX = 2;

    public static int STEP_SIZE_INDEX = 0;

    double[] massData;
    double[] equatorialData;
    double[] translationalData;
    double[] rotationalData;
    double[] utilityData;


    public OrbitalData(double[] massData, double[] equatorialData, double[] rotationalData, double[] utilityData) {
        this.massData = massData.clone();
        this.equatorialData = equatorialData.clone();
        this.rotationalData = rotationalData.clone();
        this.utilityData = utilityData.clone();
    }


    @Override
    public OrbitalData clone() {
        return new OrbitalData(massData.clone(), equatorialData.clone(), rotationalData.clone(), utilityData.clone());
    }

    public void setUtilityData(int i, double v) {
        utilityData[i] = v;
    }
    public double getUtilityData(int i) {
        return utilityData[i];
    }
    public double getMassData(int i) {
        return massData[i];
    }
    public void setRotationalData(double[] rotationalData) {
        this.rotationalData = rotationalData.clone();
    }

    @Override
    public String toString() {
        return "\n" +
                super.toString() + "\n" +
                "[m, M]: " + Arrays.toString(massData) + "\n" +
                "[τ, t, r, θ, φ, d, e, l]: " + Arrays.toString(equatorialData) + "\n" +
                "[X, Y, Z]: " + Arrays.toString(rotationalData) + "\n" +
                "[Δt]: " + Arrays.toString(utilityData) + "\n";
    }

    public double getEquatorialData(int i) {
        return equatorialData[i];
    }

    public double getRotationalData(int i) {
        return rotationalData[i];
    }

    public double[] getEquatorialData() {
        return equatorialData.clone();
    }

    public void setEquatorialData(double[] equatorialData) {
        this.equatorialData = equatorialData.clone();
    }

    public void setEquatorialData(int i, double v) {
        equatorialData[i] = v;
    }

}
