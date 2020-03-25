package engine.gui.shape;

public class CurveTo extends PathElement {
    private final float px0;
    private final float py0;
    private final float px1;
    private final float py1;
    private final float x;
    private final float y;

    public CurveTo(float px0, float py0, float px1, float py1, float x, float y) {
        this.px0 = px0;
        this.py0 = py0;
        this.px1 = px1;
        this.py1 = py1;
        this.x = x;
        this.y = y;
    }

    public float getPx0() {
        return px0;
    }

    public float getPy0() {
        return py0;
    }

    public float getPx1() {
        return px1;
    }

    public float getPy1() {
        return py1;
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
            path.curveTo(px0, py0, px1, py1, x, y);
        } else {
            float currX = path.getCurrentX();
            float currY = path.getCurrentY();
            path.curveTo(currX + px0, currY + py0, currX + px1, currY + py1, currX + x, currY + y);
        }
    }

    @Override
    public String toString() {
        return "CurveTo{" +
                "px0=" + px0 +
                ", py0=" + py0 +
                ", px1=" + px1 +
                ", py1=" + py1 +
                ", x=" + x +
                ", y=" + y +
                ", absolute=" + isAbsolute() +
                '}';
    }
}
