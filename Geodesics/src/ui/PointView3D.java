package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point3D;

public class PointView3D {
    private final SimpleStringProperty label;
    private final SimpleStringProperty x;
    private final SimpleStringProperty y;
    private final SimpleStringProperty z;
    private final SimpleStringProperty tau;

    PointView3D(String label, String x, String y, String z, String tau) {
        this.label = new SimpleStringProperty(label);
        this.x = new SimpleStringProperty(String.format("%6.3e", Double.parseDouble(x)));
        this.y = new SimpleStringProperty(String.format("%6.3e", Double.parseDouble(y)));
        this.z = new SimpleStringProperty(String.format("%6.3e", Double.parseDouble(z)));
        this.tau = new SimpleStringProperty(String.format("%6.3e", Double.parseDouble(tau)));
    }

    public PointView3D(String label, Point3D point3D, String tau) {
        this.label = new SimpleStringProperty(label);
        this.x = new SimpleStringProperty(String.format("%6.3e", point3D.getX()));
        this.y = new SimpleStringProperty(String.format("%6.3e", point3D.getY()));
        this.z = new SimpleStringProperty(String.format("%6.3e", point3D.getZ()));
        this.tau = new SimpleStringProperty(String.format("%6.3e", Double.parseDouble(tau)));
    }

    public String getLabel() {
        return label.get();
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public SimpleStringProperty labelProperty() {
        return label;
    }

    public String getX() {
        return x.get();
    }

    public void setX(String x) {
        this.x.set(x);
    }

    public SimpleStringProperty xProperty() {
        return x;
    }

    public String getY() {
        return y.get();
    }

    public void setY(String y) {
        this.y.set(y);
    }

    public String getTau() {
        return tau.get();
    }

    public void setTau(String tau) {
        this.tau.set(tau);
    }

    public SimpleStringProperty tauProperty() {
        return tau;
    }

    public SimpleStringProperty yProperty() {
        return y;
    }

    public void setPointLabels(Point3D point3D) {
        setX(Double.toString(point3D.getX()));
        setY(Double.toString(point3D.getY()));
        setZ(Double.toString(point3D.getZ()));
    }

    public String getZ() {
        return z.get();
    }

    public void setZ(String z) {
        this.z.set(z);
    }

    public SimpleStringProperty zProperty() {
        return z;
    }
}
