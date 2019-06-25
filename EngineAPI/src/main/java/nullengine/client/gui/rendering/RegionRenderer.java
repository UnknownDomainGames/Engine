package nullengine.client.gui.rendering;

import nullengine.client.gui.Component;
import nullengine.client.gui.Region;
import nullengine.client.gui.misc.Border;
import nullengine.client.rendering.RenderContext;

public class RegionRenderer<E extends Region> implements ComponentRenderer<E> {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(E region, Graphics graphics, RenderContext context) {
        region.background().getValue().render(region, graphics);
        if (region.border().isPresent()) {
            Border border = region.border().getValue();
            border.render(region, graphics);
        }
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
