package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Region;

public class RegionRenderer implements ComponentRenderer {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(Component component, Graphics graphics) {
        Region region = (Region) component;
        for (Component child : region.getUnmodifiableChildren()) {
            if (!child.visible().get()) {
                continue;
            }
            child.getRenderer().render(child, graphics);
        }
    }
}
