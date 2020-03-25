package engine.gui.graphics.shape;

import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.internal.shape.PolygonHelper;
import engine.gui.shape.Polygon;

public final class PolygonRenderer implements NodeRenderer<Polygon> {
    public static final PolygonRenderer INSTANCE = new PolygonRenderer();

    private PolygonRenderer() {
    }

    @Override
    public void render(Polygon polygon, Graphics graphics) {
        graphics.fill(PolygonHelper.getPath2D(polygon), 0, 0);
    }
}
