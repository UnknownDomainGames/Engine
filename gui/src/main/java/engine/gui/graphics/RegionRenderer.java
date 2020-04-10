package engine.gui.graphics;

import engine.gui.Region;

public class RegionRenderer<E extends Region> extends ParentRenderer<E> {

    public static final RegionRenderer<?> INSTANCE = new RegionRenderer<>();

    @Override
    public void render(E region, Graphics graphics) {
        graphics.drawBackground(region.getBackground(), region);
        graphics.drawBorder(region.getBorder(), region);
        super.render(region, graphics);
    }
}
