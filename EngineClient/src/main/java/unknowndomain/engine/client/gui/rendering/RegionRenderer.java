package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Region;

public class RegionRenderer implements ComponentRenderer {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(Component component, Graphics graphics) {
        Region region = (Region) component;
        region.background().getValue().render(component,graphics);
        for (Component child : region.getUnmodifiableChildren()) {
            if (!child.visible().get()) {
                continue;
            }
            graphics.pushClipRect(child.x().get(), child.y().get(), child.width().get(), child.height().get());
            child.getRenderer().render(child, graphics);
            graphics.popClipRect();
        }
    }
}
