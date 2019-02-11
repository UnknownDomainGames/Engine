package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.Region;
import unknowndomain.engine.client.gui.misc.Border;

public class RegionRenderer<E extends Region> implements ComponentRenderer<E> {

    public static final RegionRenderer INSTANCE = new RegionRenderer();

    @Override
    public void render(E region, Graphics graphics) {
        region.background().getValue().render(region,graphics);
        if(region.border().isPresent()){
            Border border = region.border().getValue();
            graphics.setColor(border.getColor());
            if(border.getInsets().getTop() != 0) {
                graphics.fillRect(0,0,region.width().get(), border.getInsets().getTop());
            }
            if(border.getInsets().getBottom() != 0) {
                graphics.fillRect(0, region.height().get() - border.getInsets().getBottom(),region.width().get(), border.getInsets().getBottom());
            }
            if(border.getInsets().getLeft() != 0) {
                graphics.fillRect(0,0,border.getInsets().getLeft(), region.height().get());
            }
            if(border.getInsets().getRight() != 0) {
                graphics.fillRect(region.width().get() - border.getInsets().getRight(),0,border.getInsets().getRight(), region.height().get());
            }
        }
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
