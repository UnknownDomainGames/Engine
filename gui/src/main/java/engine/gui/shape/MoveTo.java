package engine.gui.shape;

public class MoveTo extends PathElement {
    private final float x;
    private final float y;

    public MoveTo(float x, float y) {
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
        path.moveTo(x, y);
    }

    @Override
    public String toString() {
        return "MoveTo{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
