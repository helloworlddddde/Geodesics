package OrbitSolver;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Light extends Orbiter {

    private double b;


    @Override
    public void initialize(double M, int size, double r0, double phi0, double t0, int direction, double dphi0, double dt0, double plotMax) {

        super.initialize(M, size, r0, phi0, t0, direction, dphi0, dt0, plotMax);
        this. b = l/e;

    }


    @Override
    public void setE(double e) {
        this.e = e;
        b = l/e;
    }

    @Override
    public void setL(double l) {
        this.l = l;
        b = l/e;
    }




    @Override
    public double Veff(double radius) {
        return (1/(radius * radius)) * (1- 2*M/radius);
    }

    @Override
    public LineChart plot() {
        NumberAxis xAxis = new NumberAxis(); // we are gonna plot against time
        NumberAxis yAxis = new NumberAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(Math.min(-maxPotential(), 1/(b*b)) + Math.min(-maxPotential(), 1/(b*b))/10);
        yAxis.setUpperBound(Math.max(maxPotential(), 1/(b*b)) + Math.max(maxPotential(), 1/(b*b))/10);
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
            series2.getData().add(new XYChart.Data(r, 1/(b*b)));
        }

        lineChart.setCreateSymbols(false);
        return lineChart;
    }


    @Override
    public double rMax() {
        return 3*M;
    }

    @Override
    public double maxPotential() {
        return Veff(rMax());
    }

    @Override
    public String getDataText() {
        return  "M: " + M + ", "
                + "size: " + size + ", "
                + "r0: " + r[0] + ", "
                + "phi0: " + phi[0] + ", "
                + "t0: " + t[0] + ", "
                + "direction: " + direction + ", "
                + "l: " + l + ", "
                + "e: " + e + ", "
                + "b: " + b + ", "
                + "1/b^2: " + 1/(b*b) + ", "
                + "plotMax: " + plotMax + ", "
                + "rMax: " + rMax() + "\n"
                + "minimum e: " + Math.sqrt(2*Veff(r[0]) + 1);
    }

    @Override
    public void RungeKutta(double h, double turnCheck) {
        setSize(getSize());
        double direction = this.direction;
        for(int k = 1; k < size; k++) {
            double k1 = Math.sqrt((l*l)*(1/(b*b) - Veff(r[k-1])));
            double k2 = Math.sqrt((l*l)*(1/(b*b) - Veff(r[k-1] + h * k1 / 2)));
            double k3 = Math.sqrt((l*l)*(1/(b*b) - Veff(r[k-1] + h * k2 / 2)));
            double k4 = Math.sqrt((l*l)*(1/(b*b) - Veff(r[k-1] + h * k3)));

            if (1/(b*b)-Veff(r[k-1] + (1.0/turnCheck) * h * direction*(k1 + 2*k2 + 2*k3 + k4)) < 0) {
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
            t[k] = t[k-1] + h*e/(1-2*M/r[k-1]);


        }
    }
    @Override
    public void Euler(double h) {
        setSize(getSize());
        double direction = this.direction;
        for(int k = 1; k < size; k++) {

            double k1 = Math.sqrt((l*l)*(1/(b*b) - Veff(r[k-1])));

            // detecting turning point
            if (1/(b*b)-Veff(r[k-1] + h*direction*k1) < 0) {
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
            t[k] = t[k-1] + h*e/(1-2*M/r[k-1]);


        }


    }
}
