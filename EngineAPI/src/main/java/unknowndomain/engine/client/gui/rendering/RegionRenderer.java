package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Region;
import unknowndomain.engine.client.gui.misc.Border;
import unknowndomain.engine.client.rendering.RenderContext;

public class RegionRenderer<E extends Region> implements ComponentRenderer<E> {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(E region, Graphics graphics, RenderContext context) {
        region.background().getValue().render(region,graphics);
        if(region.border().isPresent()){
            Border border = region.border().getValue();
            border.render(region,graphics);
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
