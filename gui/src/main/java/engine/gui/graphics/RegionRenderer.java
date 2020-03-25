package engine.gui.graphics;

import engine.gui.Node;
import engine.gui.Region;

public class RegionRenderer<E extends Region> implements NodeRenderer<E> {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(E region, Graphics graphics) {
        graphics.drawBackground(region.getBackground(), region);
        graphics.drawBorder(region.getBorder(), region);
        for (Node child : region.getUnmodifiableChildren()) {
            if (!child.isVisible()) continue;
            graphics.pushClipRect(child.getLayoutX(), child.getLayoutY(), child.getWidth(), child.getHeight());
            child.getRenderer().render(child, graphics);
            graphics.popClipRect();
        }
    }
}
