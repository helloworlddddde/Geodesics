package OrbitSolver;

import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Box;

public class Orbiter extends Box {


    protected double plotMax;
    protected double M;
    protected int size;
    protected double[] r;
    protected double[] phi;
    protected double[] t;
    protected double e;
    protected double l;
    protected int direction;
    protected double epsilon;


    public Orbiter() {
        super(3, 3, 3);
    }

    public void initialize(double M, int size, double r0, double phi0, double t0, int direction, double dphi0, double dt0, double plotMax) {

        this.plotMax = plotMax;
        this.M = M;
        this.size = size;

        this.r = new double[size];
        this.phi = new double[size];
        this.t = new double[size];
        this.direction = direction;

        r[0] = r0;
        phi[0] = phi0;
        t[0] = t0;

        e = (1-2*M/r0)* dt0;
        l = r0 * r0 * dphi0;
        epsilon = (e*e - 1) / 2;



    }

    public void setPlotMax(double plotMax) {
        this.plotMax = plotMax;
    }

    public void setM(double m) {
        M = m;
    }

    public void setSize(int size) {

        this.size = size;

        double tempR = r[0];
        r = new double[size];
        r[0] = tempR;

        double tempPhi = phi[0];
        phi = new double[size];
        phi[0] = tempPhi;

        double tempT = t[0];
        t = new double[size];
        t[0] = tempT;


    }

    public void setR(double[] r) {
        this.r = r;
    }

    public void setPhi(double[] phi) {
        this.phi = phi;
    }

    public void setT(double[] t) {
        this.t = t;
    }

    public void setE(double e) {
        this.e = e;
        this.epsilon = (e * e - 1) / 2;
    }

    public double getM() {
        return M;
    }

    public double getE() {
        return e;
    }

    public double getL() {
        return l;
    }


    public void setL(double l) {
        this.l = l;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }




    public LineChart plot() {

        NumberAxis xAxis = new NumberAxis(); // we are gonna plot against time
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(Math.min(minPotential(), epsilon) + Math.min(minPotential(), epsilon)/10);
        yAxis.setUpperBound(Math.max(maxPotential(), epsilon) + Math.max(maxPotential(), epsilon)/10);
        //creating the line chart with two axis created above
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Realtime JavaFX Charts");
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Data Series");
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series.setName("Data Series");
        lineChart.getData().add(series);
        lineChart.getData().add(series2);
        lineChart.setTitle("Effective Potential");
        for (double r = 1; r < plotMax; r += 0.2) {
            series.getData().add(new XYChart.Data(r, Veff(r)));
            series2.getData().add(new XYChart.Data(r, epsilon));
        }

        lineChart.setCreateSymbols(false);
        return lineChart;
    }


    public double Veff(double radius) {
        return -M/radius + (l*l)/(2*radius*radius) - (M*l*l)/(radius*radius*radius);
    }

    public double rMin() {
        return ((l*l)/(2*M)) * (1+Math.sqrt(1-12*(M/l)*(M/l)));
    }

    public double rMax() {
        return ((l*l)/(2*M)) * (1-Math.sqrt(1-12*(M/l)*(M/l)));
    }

    public double minPotential() {
        return Veff(rMin());
    }

    public String getDataText() {
        return  "M: " + M + ", "
                + "size: " + size + ", "
                + "r0: " + r[0] + ", "
                + "phi0: " + phi[0] + ", "
                + "t0: " + t[0] + ", "
                + "direction: " + direction + ", "
                + "l: " + l + ", "
                + "e: " + e + ", "
                + "plotMax: " + plotMax + ", "
                + "rMax: " + rMax() + ", "
                + "rMin: " + rMin() +  "\n"
                + "minimum e: " + Math.sqrt(2*Veff(r[0]) + 1);
    }

    public double maxPotential() {
        return Veff(rMax());
    }

    public void RungeKutta(double h, double turnCheck) {
        setSize(getSize());
        double direction = this.direction;
        for(int k = 1; k < size; k++) {
            double k1 = Math.sqrt(2*(epsilon - Veff(r[k-1])));
            double k2 = Math.sqrt(2*(epsilon - Veff(r[k-1] + h * k1 / 2)));
            double k3 = Math.sqrt(2*(epsilon - Veff(r[k-1] + h * k2 / 2)));
            double k4 = Math.sqrt(2*(epsilon - Veff(r[k-1] + h * k3)));

            if (epsilon-Veff(r[k-1] + (1.0/turnCheck) * h * direction*(k1 + 2*k2 + 2*k3 + k4)) < 0) {
                if (r[k-1] + (1.0/turnCheck) * h * direction * (k1 + 2*k2 + 2*k3 + k4) < 0) {
                    break;
                }
                direction *= -1;
            }

            if (r[k-1] + (1.0/turnCheck) * h * direction * (k1 + 2*k2 + 2*k3 + k4) < 0) {
                break;
            }


            r[k] = r[k-1] + (1.0/6) * h * direction * (k1 + 2*k2 + 2*k3 + k4);
            phi[k] = phi[k-1] + h*l/Math.pow(r[k-1], 2);
            phi[k] %= 2*Math.PI;
            t[k] = t[k-1] + h*e/(1-2*M/r[k-1]);


        }

    }

    public void Euler(double h) {
        setSize(getSize());
        double direction = this.direction;
        for(int k = 1; k < size; k++) {

            double k1 = Math.sqrt(2*(epsilon - Veff(r[k-1])));

            // detecting turning point
            if (epsilon-Veff(r[k-1] + h*direction*k1) < 0) {
                if (r[k-1] + h*direction*k1 < 0) {
                    break;
                }
                direction *= -1;
            }

            if (r[k-1] + h*direction*k1 < 0) {
                break;
            }


            r[k] = r[k-1] + h*direction*k1;
            phi[k] = phi[k-1] + h*l/Math.pow(r[k-1], 2);
            phi[k] %= 2*Math.PI;
            t[k] = t[k-1] + h*e/(1-2*M/r[k-1]);


        }


    }

    public double[] getR() {
        return r;
    }

    public double[] getPhi() {
        return phi;
    }

    public double[] getT() {
        return t;
    }

    public int getSize() {
        return size;
    }



}
