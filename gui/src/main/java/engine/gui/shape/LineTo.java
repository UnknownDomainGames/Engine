package engine.gui.shape;

import engine.graphics.shape.Path2D;

public class LineTo extends PathElement {
    private final float x;
    private final float y;

    public LineTo(float x, float y) {
        this.x = x;
        this.y = y;
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
            path.lineTo(x, y);
        } else {
            path.lineTo(path.getCurrentX() + x, path.getCurrentY() + y);
        }
    }

    @Override
    public String toString() {
        return "LineTo{" +
                "x=" + x +
                ", y=" + y +
                ", absolute=" + isAbsolute() +
                '}';
    }
}
