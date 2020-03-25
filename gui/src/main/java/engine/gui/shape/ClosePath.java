package engine.gui.shape;

public class ClosePath extends PathElement {
    @Override
    public void addPath(Path2D path) {
        path.closePath();
    }
}
