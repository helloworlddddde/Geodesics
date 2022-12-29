package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Point3D;

public class PointView3D {
    private final SimpleStringProperty x;
    private final SimpleStringProperty y;
    private final SimpleStringProperty z;

    public PointView3D(Point3D point3D) {
        this.x = new SimpleStringProperty(String.format("%6.3e", point3D.getX()));
        this.y = new SimpleStringProperty(String.format("%6.3e", point3D.getY()));
        this.z = new SimpleStringProperty(String.format("%6.3e", point3D.getZ()));
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

    public SimpleStringProperty yProperty() {
        return y;
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
