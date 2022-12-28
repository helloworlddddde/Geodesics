package orbital.data;

import java.util.ArrayList;

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
    double[] rotationalData;
    double[] utilityData;


    public OrbitalData(double[] massData, double[] equatorialData, double[] rotationalData, double[] utilityData) {
        this.massData = massData.clone();
        this.equatorialData = equatorialData.clone();
        this.rotationalData = rotationalData.clone();
        this.utilityData = utilityData.clone();
    }


    public OrbitalData clone() {
        return new OrbitalData(massData.clone(), equatorialData.clone(), rotationalData.clone(), utilityData.clone());
    }

    public double getUtilityData(int i) {
        return utilityData[i];
    }
    public double getMassData(int i) {
        return massData[i];
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
