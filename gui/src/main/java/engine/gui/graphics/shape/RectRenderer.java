package engine.gui.graphics.shape;

import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.shape.Rect;

public class RectRenderer extends NodeRenderer<Rect> {
    public static final RectRenderer INSTANCE = new RectRenderer();

    @Override
    public void render(Rect rect, Graphics graphics) {
        float strokeWidth = rect.getStrokeWidth();
        if (strokeWidth > 0) {
            graphics.setColor(rect.getStrokeColor());
            graphics.fillRect(strokeWidth, strokeWidth, rect.getWidth() + strokeWidth * 2, rect.getHeight() + strokeWidth * 2);
        }
        graphics.setColor(rect.getFillColor());
        graphics.fillRect(strokeWidth, strokeWidth, rect.getWidth(), rect.getHeight());
    }
}
