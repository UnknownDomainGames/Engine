package engine.gui.shape;

public class QuadTo extends PathElement {
    private final float px;
    private final float py;
    private final float x;
    private final float y;

    public QuadTo(float px, float py, float x, float y) {
        this.px = px;
        this.py = py;
        this.x = x;
        this.y = y;
    }

    public float getPx() {
        return px;
    }

    public float getPy() {
        return py;
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
            path.quadTo(px, py, x, y);
        } else {
            float currX = path.getCurrentX();
            float currY = path.getCurrentY();
            path.quadTo(currX + px, currY + py, currX + x, currY + y);
        }
    }

    @Override
    public String toString() {
        return "QuadTo{" +
                "px=" + px +
                ", py=" + py +
                ", x=" + x +
                ", y=" + y +
                ", absolute=" + isAbsolute() +
                '}';
    }
}
