package engine.gui.shape;

import engine.graphics.shape.Path2D;

public class ArcTo extends PathElement {

    private final float radiusX;
    private final float radiusY;
    private final float xAxisRotation;
    private final boolean largeArcFlag;
    private final boolean sweepFlag;
    private final float x;
    private final float y;

    public ArcTo(float radiusX, float radiusY, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) {
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.xAxisRotation = xAxisRotation;
        this.largeArcFlag = largeArcFlag;
        this.sweepFlag = sweepFlag;
        this.x = x;
        this.y = y;
    }

    public float getRadiusX() {
        return radiusX;
    }

    public float getRadiusY() {
        return radiusY;
    }

    public float getxAxisRotation() {
        return xAxisRotation;
    }

    public boolean isLargeArcFlag() {
        return largeArcFlag;
    }

    public boolean isSweepFlag() {
        return sweepFlag;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public void addPath(Path2D path) {
        if (isAbsolute()) {
            path.arcTo(radiusX, radiusY, xAxisRotation, largeArcFlag, sweepFlag, x, y);
        } else {
            path.arcTo(radiusX, radiusY, xAxisRotation, largeArcFlag, sweepFlag, path.getCurrentX() + x, path.getCurrentY() + y);
        }
    }

    @Override
    public String toString() {
        return "ArcTo{" +
                "radiusX=" + radiusX +
                ", radiusY=" + radiusY +
                ", xAxisRotation=" + xAxisRotation +
                ", largeArcFlag=" + largeArcFlag +
                ", sweepFlag=" + sweepFlag +
                ", x=" + x +
                ", y=" + y +
                ", absolute=" + isAbsolute() +
                '}';
    }
}
