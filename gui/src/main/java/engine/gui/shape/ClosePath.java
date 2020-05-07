package engine.gui.shape;

import engine.graphics.shape.Path2D;

public class ClosePath extends PathElement {
    @Override
    public void addPath(Path2D path) {
        path.closePath();
    }
}
