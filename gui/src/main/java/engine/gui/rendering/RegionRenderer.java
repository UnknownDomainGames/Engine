package engine.gui.rendering;

import engine.gui.Node;
import engine.gui.Region;

public class RegionRenderer<E extends Region> implements ComponentRenderer<E> {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(E region, Graphics graphics) {
        graphics.drawBackground(region.background().getValue(), region);
        graphics.drawBorder(region.border().getValue(), region);
        for (Node child : region.getUnmodifiableChildren()) {
            if (!child.isVisible()) continue;
            graphics.pushClipRect(child.x().get(), child.y().get(), child.width().get(), child.height().get());
            child.getRenderer().render(child, graphics);
            graphics.popClipRect();
        }
    }
}
