package nullengine.client.gui.rendering;

import nullengine.client.gui.Component;
import nullengine.client.gui.Region;
import nullengine.client.rendering.RenderManager;

public class RegionRenderer<E extends Region> implements ComponentRenderer<E> {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(E region, Graphics graphics, RenderManager context) {
        graphics.drawBackground(region.background().getValue(), region);
        graphics.drawBorder(region.border().getValue(), region);
        for (Component child : region.getUnmodifiableChildren()) {
            if (!child.visible().get()) {
                continue;
            }
            graphics.pushClipRect(child.x().get(), child.y().get(), child.width().get(), child.height().get());
            child.getRenderer().render(child, graphics, context);
            graphics.popClipRect();
        }
    }
}
